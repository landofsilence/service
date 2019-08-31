package com.meetball.demo.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetball.demo.domain.Driver;
import com.meetball.demo.domain.Match;
import com.meetball.demo.domain.User;
import com.meetball.demo.persistence.DriverMapper;
import com.meetball.demo.persistence.UserMapper;
import com.meetball.demo.service.DriverService;
import com.meetball.demo.service.UserService;
import com.meetball.demo.socket.ClientService;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class DriverServiceImpl implements DriverService {
    @Autowired
    private DriverMapper driverMapper;

    @Override
    public List<Driver> getDriverUnpassList() {
        return driverMapper.getDriverUnpassList();
    }

    @Override
    public boolean passDriver(String userName) {
        return driverMapper.passDriver(userName);
    }

    @Override
    public boolean deleteDriver(String userName) {
        return driverMapper.deleteDriver(userName);
    }
}
