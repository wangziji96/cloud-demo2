package com.atguigu.auth.controller;

import com.atguigu.auth.SysUser;
import com.atguigu.auth.User;
import com.atguigu.auth.service.ISysUserService;
import com.atguigu.result.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private ISysUserService userService;
    //用户注册
    @PostMapping("/register")
    public Result register(@RequestBody SysUser user) {
        userService.register(user);
        return Result.success();
    }
    //用户登录
    @PostMapping("/login")
    public Result login(@Valid @RequestBody User user) {
        return userService.login(user);
    }
}
