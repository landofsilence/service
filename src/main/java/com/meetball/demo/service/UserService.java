package com.meetball.demo.service;

import com.meetball.demo.domain.Driver;
import com.meetball.demo.domain.User;
import com.meetball.demo.socket.ClientService;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;



public interface UserService {

    public void insertUser(User user);
    public User getUserByUserName(@Param("userName")String userName);
    public String login (String userName,String password);
    public String register(User user);
    public String getInfo(String userName);
    public String driverLogin(Driver driver);
    public String getDriverInfo(String userName);
    public String updateUserInfo(String json);
    //public String match(String matchJson);
    //public String client(String matchJson, ClientService clientService);
    //public String getmatchinfo(String matchJson);
    //public String updateInfo(String json);

}
