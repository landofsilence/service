package com.meetball.demo.service;

import com.meetball.demo.domain.Order;

import com.meetball.demo.socket.ClientService;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderService {
    public List<Order> getOrderList();
    public void updateOrderList(List<Order> newList);

    public String publishOrder2List(String orderJson);
    public void takeOrder(String orderJson);
    public void finishOrder(String orderJson);
    public String getUserOrderList(String orderJson);
    public String getDriverOrderList(String orderJson);
    public String getOrderInfo(String orderJson);
    public String getOrder(String orderJson);
}
