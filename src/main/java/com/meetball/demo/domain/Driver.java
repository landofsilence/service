package com.meetball.demo.domain;

import java.util.List;

public class Driver {
    private String userName;
    private String name;
    private String password;
    private String driverLicense;
    private int age;
    private boolean work;
    private boolean pass;
    private String telephone;
    private List<String> listOrderId;

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

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public boolean isWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
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
