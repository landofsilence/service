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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    private Map<String, Order> orderList = new HashMap<String, Order>();

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
    public Map<String, Order> getOrderList() {
        return orderList;
    }

    @Override
    public void updateOrderList(Map<String, Order> newList) {
        orderList = newList;
    }

    @Override
    public void publishOrder2List(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String cookie = jsonObject.getString("cookie");
        String orderStr = jsonObject.getString("order");
        System.out.println(orderStr);

        Gson gson = new Gson();
        Order order = gson.fromJson(orderStr, new TypeToken<Order>(){}.getType());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String ownerName = getInstanceCMap().get(cookie);
        order.setOrderId(order.getOwnerName() + sdf.format(date));
        order.setBeginStr(date);

        JSONObject returnJson = new JSONObject();
        orderList.put(order.getOrderId(), order);
        returnJson.put("orderList",orderList);
        System.out.println(returnJson.toString());
    }

    @Override
    public void takeOrder(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String cookie = jsonObject.getString("cookie");
        String orderId = jsonObject.getString("orderId");
        String userName = jsonObject.getString("userName");

        Gson gson = new Gson();
        Order order = orderList.get(orderId);
        order.setDriverName(userName);

        JSONObject returnJson = new JSONObject();
        orderList.put(order.getOrderId(), order);
        returnJson.put("orderList",orderList);
        System.out.println(returnJson.toString());
    }

    @Override
    public void finishOrder(String orderJson) {
        JSONObject jsonObject = JSONObject.fromObject(orderJson);
        String cookie = jsonObject.getString("cookie");
        String orderId = jsonObject.getString("orderId");

        Gson gson = new Gson();
        Order order = orderList.get(orderId);
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
}
