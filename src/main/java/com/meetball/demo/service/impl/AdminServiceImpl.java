package com.meetball.demo.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetball.demo.domain.Admin;
import com.meetball.demo.domain.Match;
import com.meetball.demo.domain.User;
import com.meetball.demo.persistence.AdminMapper;
import com.meetball.demo.persistence.UserMapper;
import com.meetball.demo.service.AdminService;
import com.meetball.demo.service.UserService;
import com.meetball.demo.socket.ClientService;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    public Admin login (Admin admin){
        return adminMapper.login(admin);
    }
}
