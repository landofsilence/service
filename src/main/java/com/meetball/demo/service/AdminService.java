package com.meetball.demo.service;

import com.meetball.demo.domain.Admin;
import com.meetball.demo.domain.User;
import org.apache.ibatis.annotations.Param;


public interface AdminService {
    public Admin login(Admin admin);
    //public String match(String matchJson);
    //public String client(String matchJson, ClientService clientService);
    //public String getmatchinfo(String matchJson);
    //public String updateInfo(String json);

}
