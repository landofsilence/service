package com.meetball.demo.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetball.demo.domain.Match;
import com.meetball.demo.domain.Order;
import com.meetball.demo.domain.User;
import com.meetball.demo.persistence.OrderMapper;
import com.meetball.demo.service.OrderService;
import com.meetball.demo.socket.ClientService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    public static final int CANCEL = 1;
    public static final int ONCAR = 2;
    public static final int FINISH = 3;

    private List<Order> orderList = new ArrayList<Order>();

    public static OrderServiceImpl instance;
    String separator = System.getProperty("line.separator");
    private static HashMap<String, ClientService> socketMap;//存放数据为，userName-对应ClientService
    private static HashMap<String, String> cookieMap;//存放数据为,cookie-对应userName

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
    public void takeOrder(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String cookie = jsonObject.getString("cookie");
        String orderId = jsonObject.getString("orderId");
        String userName = jsonObject.getString("userName");

        Gson gson = new Gson();
        Order order = getOrderByID(orderId);
        order.setDriverName(userName);

        JSONObject returnJson = new JSONObject();
        orderList.set(getOrderIndexByID(orderId), order);
        returnJson.put("orderList",orderList);
        System.out.println(returnJson.toString());
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
            if (orderList.get(i).getDriverName().equals(ownerName)){
                result = orderList.get(i);
                break;
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

    @Override
    public String getOrder(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        double lat = jsonObject.getDouble("lat");
        double lon = jsonObject.getDouble("lon");
        String orderId = jsonObject.getString("orderId");
        System.out.println("orderId =============== " + orderId);
        JSONObject returnJson = new JSONObject();
        Order order = null;

        int index = -1;
        if (getOrderByID(orderId) != null){
            index = getOrderIndexByID(orderId);
        }

        if (index == orderList.size() - 1){
            index = -1;
        }

        for (int i = index + 1; i < orderList.size(); i++){
            order = orderList.get(i);
            if (order.getOwnerLat() < lat){  //判断是否在司机一定距离内，先随便写一下
                System.out.println("i =============== " + i);
                break;
            }
            order = null;
        }
        returnJson.put("order", order);
        return returnJson.toString();
    }

    @Override
    public String orderAction(String orderJson, int action) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String orderId = jsonObject.getString("orderId");
        JSONObject returnObject = new JSONObject();
        Order order;
        switch (action){
            case CANCEL:
                order = getOrderByID(orderId);
                if (!order.getBeginStr().isEmpty()){
                    returnObject.put("result", 0);
                } else {
                    orderList.remove(getOrderIndexByID(orderId));
                    returnObject.put("result", 1);
                }
                returnObject.put("action", CANCEL);
                break;
            case ONCAR:
                order = getOrderByID(orderId);
                order.setBeginStr(new Date());
                orderList.set(getOrderIndexByID(orderId), order);
                returnObject.put("action", ONCAR);
                returnObject.put("result", 1);
                break;
            case FINISH:
                order = getOrderByID(orderId);
                order.setEndStr(new Date());
                orderMapper.insertOrder(order);
                orderList.remove(order.getOrderId());
                returnObject.put("action", FINISH);
                returnObject.put("result", 1);
                break;
        }
        return returnObject.toString();
    }
}
