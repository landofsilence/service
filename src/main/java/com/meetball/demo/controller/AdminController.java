package com.meetball.demo.controller;

import com.meetball.demo.domain.Admin;
import com.meetball.demo.domain.Driver;
import com.meetball.demo.service.AdminService;
import com.meetball.demo.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private DriverService driverService;

    @GetMapping("/admin/login")
    public String viewLogin(Model model) {
        model.addAttribute("msg", null);
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String login(Admin admin, Model model) {
        Admin loginAdmin = adminService.login(admin);

        if (loginAdmin != null){
            List<Driver> driverUnpassList = driverService.getDriverUnpassList();
            model.addAttribute("driverUnpassList", driverUnpassList);
            model.addAttribute("msg", null);
            return "admin/index";
        } else {
            model.addAttribute("msg", "用户名或密码错误!!!");
            return "admin/login";
        }
    }

    @GetMapping("/admin/passDriver")
    public String passDriver(String userName, Model model) {
        if(driverService.passDriver(userName)) {
            model.addAttribute("msg", "通过成功!!!");
        } else {
            model.addAttribute("msg", "通过失败!!!");
        }
        List<Driver> driverUnpassList = driverService.getDriverUnpassList();
        model.addAttribute("driverUnpassList", driverUnpassList);
        return "admin/index";
    }

    @GetMapping("/admin/deleteDriver")
    public String deleteDriver(String userName, Model model) {
        if(driverService.deleteDriver(userName)) {
            model.addAttribute("msg", "不通过成功!!!");
        } else {
            model.addAttribute("msg", "不通过失败!!!");
        }
        List<Driver> driverUnpassList = driverService.getDriverUnpassList();
        model.addAttribute("driverUnpassList", driverUnpassList);
        return "admin/index";
    }
}
