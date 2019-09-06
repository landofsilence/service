package com.meetball.demo.domain;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    protected String userName;
    protected String name;
    protected String password;
    protected int age;
    protected String telephone;
    protected String carNumber;
    protected List<String> listOrderId;
    protected int imageNum;

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telphone) {
        this.telephone = telphone;
    }

    public List<String> getListOrderId() {
        return listOrderId;
    }

    public void setListOrderId(List<String> listOrderId) {
        this.listOrderId = listOrderId;
    }
}
