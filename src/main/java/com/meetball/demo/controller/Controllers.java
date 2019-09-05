package com.meetball.demo.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetball.demo.domain.Driver;
import com.meetball.demo.domain.Order;
import com.meetball.demo.domain.User;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import com.meetball.demo.service.*;

@Controller
public class Controllers {
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    private Map<String, Order> orderList;

    @ResponseBody
    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String passwd = request.getParameter("password");
        return userService.login(userName,passwd);

    }

    @ResponseBody
    @RequestMapping("/driverLogin")
    public String driverLogin(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String passwd = request.getParameter("password");
        Driver driver = new Driver();
        driver.setUserName(userName);
        driver.setPassword(passwd);
        return userService.driverLogin(driver);

    }

    @ResponseBody
    @RequestMapping("/register")
    public String register(HttpServletRequest request, HttpServletResponse response) {
        User cacheUser = new User();
        /*Gson gson = new Gson();
        User user = gson.fromJson(request.getParameter("user"), new TypeToken<User>() {} .getType());*/
        cacheUser.setUserName(request.getParameter("userName"));
        cacheUser.setName(request.getParameter("name"));
        cacheUser.setPassword(request.getParameter("password"));
        cacheUser.setAge(Integer.valueOf(request.getParameter("age")));
        cacheUser.setCarNumber(request.getParameter("carNumber"));
        cacheUser.setTelephone(request.getParameter("telephone"));

        return userService.register(cacheUser);
    }

    @ResponseBody
    @RequestMapping("/getinfo")
    public String getInfo(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        return userService.getInfo(userName);
    }

    @ResponseBody
    @RequestMapping("/getOrderList")
    public String getOrderList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject returnJson = new JSONObject();
        orderList = orderService.getOrderList();
        returnJson.put("orderList",orderList);
        return returnJson.toString();
    }

    @ResponseBody
    @RequestMapping("/finishOrder")
    public String finishOrder(HttpServletRequest request, HttpServletResponse response) {
        String ownerName = request.getParameter("ownerName");
        double ownerLon = Double.valueOf(request.getParameter("ownerLon"));//起始经度
        double ownerLat = Double.valueOf(request.getParameter("ownerLat"));//起始纬度
        double endLon = Double.valueOf(request.getParameter("endLon"));//目的地经度
        double endLat = Double.valueOf(request.getParameter("endLat"));//目的地纬度
        String carNumber = request.getParameter("carNumber");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Order order = new Order();

        order.setOrderId(ownerName + sdf.format(date));
        order.setOwnerName(ownerName);
        order.setOwnerLon(ownerLon);
        order.setOwnerLat(ownerLat);
        order.setEndLon(endLon);
        order.setEndLat(endLat);
        order.setCarNumber(carNumber);
        order.setBeginStr(date);

        orderList = orderService.getOrderList();
        orderList.put(order.getOrderId(), order);

        orderService.updateOrderList(orderList);
        return "1";
    }
}
