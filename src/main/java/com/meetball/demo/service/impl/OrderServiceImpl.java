package com.meetball.demo.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetball.demo.domain.Match;
import com.meetball.demo.domain.Order;
import com.meetball.demo.domain.User;
import com.meetball.demo.persistence.OrderMapper;
import com.meetball.demo.service.OrderService;
import com.meetball.demo.socket.ClientService;
import com.meetball.demo.socket.SocketServer;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    private static String fetch = System.getProperty("line.separator");

    public static final int CANCEL = 1;
    public static final int ONCAR = 2;
    public static final int FINISH = 3;

    private List<Order> orderList = new ArrayList<Order>();

    public static OrderServiceImpl instance;
    String separator = System.getProperty("line.separator");
    private static HashMap<String,Double> mylocationLat;
    private static HashMap<String,Double> mylocationLon;

    public static HashMap<String,Double> getLocationMapLat(){//控制单例cookiemap
        if(mylocationLat == null)
            mylocationLat = new HashMap<String,Double>();
        return mylocationLat;
    }

    public static HashMap<String,Double> getLocationMapLon(){//控制单例cookiemap
        if(mylocationLon == null)
            mylocationLon = new HashMap<String,Double>();
        return mylocationLon;
    }

    public OrderServiceImpl(){
        instance = this;
    }

    public static OrderServiceImpl getInstance() {
        return instance;
    }

    @Override
    public List<Order> getOrderList() {
        return orderList;
    }

    @Override
    public void updateOrderList(List<Order> newList) {
        orderList = newList;
    }

    @Override
    public String publishOrder2List(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String cookie = jsonObject.getString("cookie");
        String orderStr = jsonObject.getString("order");
        System.out.println(orderStr);

        Gson gson = new Gson();
        Order order = gson.fromJson(orderStr, new TypeToken<Order>(){}.getType());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String ownerName = order.getOwnerName();
        order.setOrderId(order.getOwnerName() + sdf.format(date));

        JSONObject returnJson = new JSONObject();
        if (getOrderExist(ownerName)){
            returnJson.put("result",2);
        } else {
            orderList.add(order);
            returnJson.put("result",1);
        }

        return returnJson.toString();
    }

    @Override
    public String takeOrder(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String orderId = jsonObject.getString("orderId");
        String userName = jsonObject.getString("userName");


        Gson gson = new Gson();
        Order order = getOrderByID(orderId);

        JSONObject returnJson = new JSONObject();
        if (order.getDriverName() == null) {    //该订单尚未被接单
            order.setDriverName(userName);
            orderList.set(getOrderIndexByID(orderId), order);
            ClientService clientService = UserServiceImpl.getInstance().getInstanceSMap().get(order.getOwnerName());
            returnJson.put("order", order);
            returnJson.put("isSelf", true);
            clientService.sendMessage("<getOrderByIDRe>" + fetch + returnJson.toString() + fetch + "</getOrderByIDRe>");
        } else {
            returnJson.put("result", 2);
        }

        return  returnJson.toString();
    }

    @Override
    public void finishOrder(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String cookie = jsonObject.getString("cookie");
        String orderId = jsonObject.getString("orderId");

        Gson gson = new Gson();
        Order order = getOrderByID(orderId);
        Date date = new Date();
        order.setEndStr(date);

        orderMapper.insertOrder(order);
        JSONObject returnJson = new JSONObject();
        orderList.remove(order.getOrderId());
        returnJson.put("orderList",orderList);
        System.out.println(returnJson.toString());
    }

    @Override
    public String getUserOrderList(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String userName = jsonObject.getString("userName");
        JSONObject returnJson = new JSONObject();
        List<String> orderList = orderMapper.getUserOrderList(userName);
        returnJson.put("orderList", orderList);
        return returnJson.toString();
    }

    @Override
    public String getDriverOrderList(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String userName = jsonObject.getString("userName");
        JSONObject returnJson = new JSONObject();
        List<String> orderList = orderMapper.getDriverOrderList(userName);
        returnJson.put("orderList", orderList);
        return returnJson.toString();
    }

    @Override
    public String getOrderInfo(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String orderId = jsonObject.getString("orderId");
        JSONObject returnJson = new JSONObject();
        Order order = orderMapper.getOrderInfo(orderId);
        returnJson.put("order", order);
        return returnJson.toString();
    }

    public String askMyOrder(String json){
        JSONObject jsonObject = JSONObject.fromObject(json);
        String userName = (String)jsonObject.get("userName");
        return "error";

    }

    public Order getOrderByID(String orderId) {
        Order order = null;
        for (int i = 0; i < orderList.size(); i++){
            if (orderList.get(i).getOrderId().equals(orderId)){
                order = orderList.get(i);
                break;
            }
        }
        return order;
    }

    public int getOrderIndexByID(String orderId) {
        int index = -1;
        for (int i = 0; i < orderList.size(); i++){
            if (orderList.get(i).getOrderId().equals(orderId)){
                index = i;
                break;
            }
        }
        return index;
    }

    public Order getDriverOrderExist(String ownerName) {
        Order result = null;
        for (int i = 0; i < orderList.size(); i++){
            if (orderList.get(i).getDriverName() != null) {
                if (orderList.get(i).getDriverName().equals(ownerName)) {
                    result = orderList.get(i);
                    break;
                }
            }
        }
        return result;
    }

    public boolean getOrderExist(String ownerName) {
        boolean result = false;
        for (int i = 0; i < orderList.size(); i++){
            if (orderList.get(i).getOwnerName().equals(ownerName)){
                result = true;
                break;
            }
        }
        return result;
    }
    public Order getOrderExist_Order(String ownerName) {
        Order result = null;
        for (int i = 0; i < orderList.size(); i++){
            if (orderList.get(i).getOwnerName().equals(ownerName)){
                result = orderList.get(i);
                break;
            }
        }
        return result;
    }

    public String myLocation(JSONObject jsonObject,ClientService clientService){
        HashMap<String,Double> latitudeMap = getLocationMapLat();
        HashMap<String,Double> longtitudeMap = getLocationMapLon();
        Double latitude = (Double) jsonObject.get("latitude");
        Double longtitude = (Double) jsonObject.get("longtitude");
        latitudeMap.put(clientService.userName,latitude);
        longtitudeMap.put(clientService.userName,longtitude);
        JSONObject returnJson = new JSONObject();
        if(clientService.isUser){
            Order order = getOrderExist_Order(clientService.userName);
            if(order != null && order.getDriverName() != null){
            String driverName = order.getDriverName();
            Double moneyCount = order.getAmount();
            if(moneyCount!=null)
            {
                returnJson.put("moneyCount",moneyCount);
            }else{
                returnJson.put("moneyCount",0);
            }
            returnJson.put("latitude",latitudeMap.get(driverName));
            returnJson.put("longtitude",longtitudeMap.get(driverName));
            }
            else {
                returnJson.put("no_one",true);
            }
        }
        else{
            Order order = getDriverOrderExist(clientService.userName);
            Double moneyCount = (Double) jsonObject.get("moneyCount");
            order.setAmount(moneyCount);
            if(order != null && order.getOwnerName() != null){
                String ownerName = order.getOwnerName();
                returnJson.put("latitude",latitudeMap.get(ownerName));
                returnJson.put("longtitude",longtitudeMap.get(ownerName));
                returnJson.put("moneyCount",moneyCount);
            }
            else {
                returnJson.put("no_one",true);
            }
        }
        return returnJson.toString();
    }

    @Override
    public String getOrder(String orderJson,String userName) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        double lat = jsonObject.getDouble("lat");
        double lon = jsonObject.getDouble("lon");
        getLocationMapLat().put(userName,lat);
        getLocationMapLon().put(userName,lon);
        String orderId = jsonObject.getString("orderId");
        JSONObject returnJson = new JSONObject();
        Order order = null;

        int index = -1;
        if (getOrderByID(orderId) != null){
            index = getOrderIndexByID(orderId);
        }

        if (index == orderList.size() - 1){
            index = -1;
        }

        Order cacheOrder = null;

        boolean isCircle = false;
        for (int i = index + 1; i < orderList.size(); i++){
            cacheOrder = orderList.get(i);
            if (cacheOrder.getDriverName()==null) {    //该订单尚未被接单
                if (getmeter(cacheOrder.getOwnerLon(), cacheOrder.getOwnerLat(), lon, lat) < 3000) {  //判断是否在司机一定距离内，先随便写一下
                    order = cacheOrder;
                    break;
                }
            }
            if (i == orderList.size() - 1){
                i = -1;
                isCircle = true;
            }
            if(isCircle && i == index) {
                order = new Order();
                order.setOrderId("-1");
                break;
            }
        }

        if (order == null){
            order = new Order();
            order.setOrderId("-1");
        }

        returnJson.put("order", order);
        return returnJson.toString();
    }

    private static final double EARTH_RADIUS = 6378.137;

    private static double rad(double d){
        return d * Math.PI / 180.0;
    }

    /**
     * 根据经纬度算距离
     * @param long1
     * @param lat1
     * @param long2
     * @param lat2
     * @return
     */
    public static double getmeter(double long1, double lat1, double long2, double lat2) {
        double a, b, d, sa2, sb2;
        lat1 = rad(lat1);
        lat2 = rad(lat2);
        a = lat1 - lat2;
        b = rad(long1 - long2);

        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2   * EARTH_RADIUS
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        d= d * 1000;
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        return bg.doubleValue();

    }

    @Override
    public String orderAction(String orderJson, int action) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String orderId = jsonObject.getString("orderId");
        JSONObject returnObject = new JSONObject();
        Order order;
        order = getOrderByID(orderId);
        ClientService clientService = UserServiceImpl.getInstance().getInstanceSMap().get(order.getOwnerName());
        JSONObject jsonClient = new JSONObject();
        switch (action){
            case CANCEL:
                if (clientService.isUser) {
                    if (order.getBeginStr() != null) {
                        returnObject.put("result", 0);
                    } else {
                        orderList.remove(getOrderIndexByID(orderId));
                        returnObject.put("result", 1);
                        jsonClient.put("order", order);
                        jsonClient.put("isSelf", true);
                        clientService.sendMessage("<getOrderByIDRe>" + fetch + jsonClient.toString() + fetch + "</getOrderByIDRe>");
                    }
                } else {
                    order.setDriverName(null);
                    orderList.set(getOrderIndexByID(orderId), order);
                }
                returnObject.put("action", CANCEL);
                break;
            case ONCAR:
                order.setBeginStr(new Date());
                orderList.set(getOrderIndexByID(orderId), order);
                returnObject.put("action", ONCAR);
                returnObject.put("result", 1);
                jsonClient.put("order", order);
                jsonClient.put("isSelf", true);
                clientService.sendMessage("<getOrderByIDRe>" + fetch + jsonClient.toString() + fetch + "</getOrderByIDRe>");
                break;
            case FINISH:
                order.setEndStr(new Date());
                //orderMapper.insertOrder(order);
                //orderList.remove(getOrderIndexByID(order.getOrderId()));
                returnObject.put("action", FINISH);
                returnObject.put("result", 1);
                jsonClient.put("order", order);
                jsonClient.put("isSelf", true);
                clientService.sendMessage("<getOrderByIDRe>" + fetch + jsonClient.toString() + fetch + "</getOrderByIDRe>");
                break;
        }
        return returnObject.toString();
    }

    @Override
    public String updateComment(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String orderId = jsonObject.getString("orderId");
        String comment = jsonObject.getString("comment");
        int star = jsonObject.getInt("star");
        Order order = getOrderByID(orderId);
        orderMapper.insertOrder(order);
        if (star == 5){
            orderMapper.addFavorite(order.getOwnerName(),order.getDriverName() );
        }
        orderList.remove(getOrderIndexByID(order.getOrderId()));
        JSONObject returnJson = new JSONObject();
        if (orderMapper.updateComment(orderId, star, comment)){
            returnJson.put("result", 1);
        } else {
            returnJson.put("result", -1);
        }
        return returnJson.toString();
    }
}
