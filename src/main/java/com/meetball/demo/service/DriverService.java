package com.meetball.demo.service;

import com.meetball.demo.domain.Driver;
import com.meetball.demo.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface DriverService {
    public List<Driver> getDriverUnpassList();
    public boolean passDriver(String userName);
    public boolean deleteDriver(String userName);
    public boolean getDriverExist(String userName);
    public Driver driverApplyLogin(String userName, String password);
    public boolean submitDriverApply(Driver driver);
    //public String match(String matchJson);
    //public String client(String matchJson, ClientService clientService);
    //public String getmatchinfo(String matchJson);
    //public String updateInfo(String json);

}
