package com.meetball.demo.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetball.demo.domain.Driver;
import com.meetball.demo.domain.Match;
import com.meetball.demo.domain.User;
import com.meetball.demo.persistence.DriverMapper;
import com.meetball.demo.persistence.UserMapper;
import com.meetball.demo.service.UserService;
import com.meetball.demo.socket.ClientService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.Socket;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private DriverMapper driverMapper;
    @Autowired
    private UserMapper usermapper;

    public static UserServiceImpl instance;

    String separator = System.getProperty("line.separator");

    private static HashMap<String, ClientService> socketMap;//存放数据为，userName-对应ClientService

    private static HashMap<String,String> cookieMap;//存放数据为,cookie-对应userName

    public static HashMap<String,String> getInstanceCMap(){//控制单例cookiemap
        if(cookieMap == null)
            cookieMap = new HashMap<String,String>();
        return cookieMap;
    }

    public static HashMap<String,ClientService> getInstanceSMap(){//控制单例cookiemap
        if(socketMap == null)
            socketMap = new HashMap<String,ClientService>();
        return socketMap;
    }

    public UserServiceImpl(){
        instance = this;
    }

    public User getUserByUserName(@Param("userName")String userName){
        return usermapper.getUserByUserName(userName);
    }

    public void insertUser(User user){
        usermapper.insertUser(user);
    }

    public String login (String userName,String password){
        User test = getUserByUserName(userName);
        JSONObject returnJson = new JSONObject();

        if(test!=null && password.equals(test.getPassword()))
        {
            returnJson.put("result",1);
            String cookie = fromNameGetCookie(userName);
            if(cookie == null)
                cookie = getNewCookie();
            returnJson.put("cookie",cookie);
            getInstanceCMap().put(cookie,userName);
            return returnJson.toString();
        }
        else
        {
            returnJson.put("result",2);
            return returnJson.toString();
        }
    }

    public String register(User user){
        JSONObject returnJson = new JSONObject();
        User test = usermapper.getUserByUserName(user.getUserName());
        if(test != null)
        {
            returnJson.put("result",2);
            return returnJson.toString();
        }
        else{
            usermapper.insertUser(user);
            returnJson.put("result",1);
            return returnJson.toString();
        }
    }

    public String getInfo(String userName){
        JSONObject returnJson = new JSONObject();
        User target = usermapper.getUserByUserName(userName);
        target.setPassword(null);
        returnJson.put("user",target);
        return returnJson.toString();

    }

    public static String fromNameGetCookie(String value) {
        Set set = getInstanceCMap().entrySet();

        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(value)) {
                String s = (String) entry.getKey();
                return s;
            }
        }
        return null;
    }

    public String getNewCookie(){
        String string = "";
        // 循环得到10个字母
        for(int i = 0; i < 10; i++) {
            // 得到随机字母
            char c = (char) ((Math.random() * 26) + 97);
            // 拼接成字符串de'l
            string += (c + "");
        }
        //若cookie不存在，可直接采用，若存在则递归获取
        if(getInstanceCMap().get(string) == null){
            return string;
        }else
            return getNewCookie();


    }

    public String match(String matchJson){
        JSONObject jsonObject = JSONObject.fromObject(matchJson);
        String cookie = jsonObject.getString("cookie");
        String matchStr = jsonObject.getString("match");
        System.out.println(matchStr);

        Gson gson = new Gson();
        Match mac = gson.fromJson(matchStr,new TypeToken<Match>(){}.getType());

        String userName = getInstanceCMap().get(cookie);

        JSONObject returnJson = new JSONObject();
        //List<Match> allMatch = usermapper.getAllMatch(mac.getMethod());
        double x1 = mac.getLocation_lat();
        double y1 = mac.getLocation_lng();
        Match result = null;
        /*for(int i = 0;i<allMatch.size();i++)//模式优先级最高
        {
            Match cache = allMatch.get(i);
            Date begin = cache.getBeginTime();
            Date end = cache.getEndTime();
            int timelang = 0;//时间差优先级次之

            timelang += Math.abs((int) ((mac.getBeginTime().getTime() - begin.getTime())/ (1000 * 60)));
            timelang += Math.abs((int) ((mac.getEndTime().getTime() - end.getTime())/ (1000 * 60)));
            if(timelang > 60)//不超过一小时
                continue;
            double x2 = cache.getLocation_lat();
            double y2 = cache.getLocation_lng();

            double _x = Math.abs(x1-x2);
            double _y = Math.abs(y1-y2);
            double r = (double)Math.sqrt(_x*_x+_y*_y);

            System.out.println("距离判断：" + _x + "-" + _y);

            if(r > 0.03)//第三重判断，距离小于0.03单位r
                continue;

            result = cache;
            continue;
        }
        if(result != null){//如果前方发现了可以匹配的已有房间
            String s =result.getUserName();
            List<String>users = Arrays.asList(s.split("\\|"));
            s = s + userName + "|";
            result.setUserName(s);

            User user = new User();
            user.setUserName(userName);


            if(users.size() == (result.getMethod() * 2 - 1))
            {
                //满人语句
                returnJson.put("result",1);
                result.setStatus(2);
            }else{
                //未满语句
                returnJson.put("result",2);
            }

            JSONArray userList = new JSONArray();
            for (int i = 0;i<users.size();i++)
                userList.add(users.get(i));
            userList.add(userName);

            returnJson.put("userName",userList);
            returnJson.put("matchID",result.getMatchID());
            usermapper.updateMatch(result);
            usermapper.updateUser(user);

            for(int i = 0;i < users.size();i++)
            //群发新成员加入信息
            {
                ClientService clientService = getInstanceSMap().get(users.get(i));
                clientService.sendMessage("<matchRe>" + separator + returnJson.toString() + separator + "</matchRe>");
            }

        }
        else{//如果没有发现匹配的房间则创建新房间
            mac.setUserName(userName + "|");
            usermapper.insertMatch(mac);
            returnJson.put("result",2);
            JSONArray userList = new JSONArray();
            userList.add(userName);
            returnJson.put("userName",userList);
            returnJson.put("matchID",mac.getMatchID());
        }*/

        return  returnJson.toString();

    }

    public String client(String matchJson,ClientService clientService){
        JSONObject jsonObject = JSONObject.fromObject(matchJson);
        String cookie = (String)jsonObject.get("cookie");
        String userName = getInstanceCMap().get(cookie);
        JSONObject returnJson = new JSONObject();
        if(userName != null){
        getInstanceSMap().put(userName,clientService);
        clientService.userName = userName;
        returnJson.put("result",1);
        returnJson.put("userName",userName);
        }else{
            returnJson.put("result",-1);
        }
        return returnJson.toString();

    }

    public String getmatchinfo(String matchJson){
        JSONObject jsonObject = JSONObject.fromObject(matchJson);
        int matchID = (int)jsonObject.get("matchID");
        //Match result = usermapper.getMatchInfo(matchID);
        JSONObject returnJson = new JSONObject();
        //returnJson.put("match",result);
        return  returnJson.toString();
    }

    public String updateInfo(String json){
        Gson gson = new Gson();
        User user = gson.fromJson(json,new TypeToken<User>(){}.getType());
        //usermapper.updateUser(user);
        JSONObject returnJson = new JSONObject();
        returnJson.put("result",1);
        return returnJson.toString();
    }

    public int addImage(String userName){
        int number = usermapper.getImageNum(userName);
        usermapper.setImage(userName,number + 1);
        return number + 1;
    }

    public String deleteImage(String userName,String matchJson){
        JSONObject jsonObject = JSONObject.fromObject(matchJson);
        int order = jsonObject.getInt("order");
        int type = jsonObject.getInt("type");
        if(type == 2)
        {
            int number = usermapper.getImageNum(userName);
            String fileseparator = System.getProperty("file.separator");
            File beDeleted = new File(ClientService.getAbsolutePath() + fileseparator + userName + "_image_" +  order + ".jpg");
            delFile(beDeleted);
            File last = new File(ClientService.getAbsolutePath() + fileseparator + userName + "_image_" +  number + ".jpg");
            last.renameTo(beDeleted);
            usermapper.setImage(userName,number - 1);
        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("result",1);
        returnJson.put("order",order);
        return returnJson.toString();

    }

    public static UserServiceImpl getInstance(){
        return instance;
    }

    @Override
    public String driverLogin(Driver driver) {
        JSONObject returnJson = new JSONObject();
        if (driverMapper.getDriverPass(driver.getUserName()) != null){
            Driver loginDriver = driverMapper.getDriver(driver.getUserName());
            if (loginDriver != null){
                returnJson.put("result",1);
                String cookie = fromNameGetCookie(loginDriver.getUserName());
                if(cookie == null)
                    cookie = getNewCookie();
                returnJson.put("cookie",cookie);
                getInstanceCMap().put(cookie,loginDriver.getUserName());
            } else {
                returnJson.put("result",2);
            }
        } else {
            returnJson.put("result",3);
        }
        return returnJson.toString();
    }

    @Override
    public String getDriverInfo(String userName){
        JSONObject returnJson = new JSONObject();
        User target = driverMapper.getDriver(userName);
        target.setPassword(null);
        returnJson.put("user",target);
        return returnJson.toString();

    }

    static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isFile()) {
            return file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
            return file.delete();
        }
    }

    @Override
    public String updateUserInfo(String json) {
        JSONObject jsonInfo = JSONObject.fromObject(json);
        JSONObject returnJson = new JSONObject();
        User user = new User();
        user.setUserName(jsonInfo.getString("userName"));
        user.setAge(jsonInfo.getInt("age"));
        user.setCarNumber(jsonInfo.getString("carNumber"));

        if (usermapper.updateUserInfo(user)){
            returnJson.put("result",1);
        } else {
            returnJson.put("result",0);
        }

        return returnJson.toString();
    }
}
