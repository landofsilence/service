package com.meetball.demo.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 一个新的订单信息提交时只填入前7项。
 * 当被接单后，将代驾司机用户名填入driverName，同时填入当前时间beginStr。
 * 本订单完成后将结束时间填入endStr。
 */
public class Order {
    private String orderId;
    private String ownerName;
    private double ownerLon;//起始经度
    private double ownerLat;//起始纬度
    private double endLon;//目的地经度
    private double endLat;//目的地纬度
    private String carNumber;
    private String driverName;
    private String beginStr;
    private String endStr;
    private double amount;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getOwnerLon() {
        return ownerLon;
    }

    public void setOwnerLon(double ownerLon) {
        this.ownerLon = ownerLon;
    }

    public double getOwnerLat() {
        return ownerLat;
    }

    public void setOwnerLat(double ownerLat) {
        this.ownerLat = ownerLat;
    }

    public double getEndLon() {
        return endLon;
    }

    public void setEndLon(double endLon) {
        this.endLon = endLon;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getBeginStr() {
        return beginStr;
    }

    public void setBeginStr(Date beginTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(beginTime);
        beginStr = dateString;
    }

    public String getEndStr() {
        return endStr;
    }

    public void setEndStr(Date endTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(endTime);
        endStr = dateString;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
