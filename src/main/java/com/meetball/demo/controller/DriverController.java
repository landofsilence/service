package com.meetball.demo.controller;

import com.meetball.demo.domain.Driver;
import com.meetball.demo.service.DriverService;
import com.meetball.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DriverController {
    @Autowired
    private DriverService driverService;
    @Autowired
    private UserService userService;

    @GetMapping("/driver/login")
    public String viewLogin(Model model) {
        model.addAttribute("msg", null);
        return "driver/login";
    }

    @PostMapping("/driver/login")
    public String login(Driver driver, Model model) {
        if (driverService.getDriverExist(driver.getUserName())){
            model.addAttribute("msg", "您已经提交过申请或已成为司机！！！");
            return "driver/login";
        }
        Driver loginDriver = driverService.driverApplyLogin(driver.getUserName(), driver.getPassword());

        if (loginDriver != null){
            model.addAttribute("userName", loginDriver.getUserName());
            return "driver/index";
        } else {
            model.addAttribute("msg", "用户名或密码错误！！！");
            return "driver/login";
        }

    }

    @PostMapping("/driver/submit")
    public String submit(Driver driver, String userName, Model model) {
        if (driverService.getDriverExist(driver.getUserName())){
            model.addAttribute("msg", "您已经提交过申请或已成为司机！！！");
            return "driver/login";
        }
        if (driverService.submitDriverApply(driver)) {
            model.addAttribute("msg", "您已成功提交申请，请耐心等待审核。");
            return "driver/success";
        } else {
            model.addAttribute("msg", "提交申请失败，请稍后再试。");
            return "driver/index";
        }
    }
}
