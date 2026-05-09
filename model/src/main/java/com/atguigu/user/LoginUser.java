package com.atguigu.user;

import lombok.Data;

import java.util.List;
@Data
public class LoginUser {
    private String userId;
    private List<String> roles;
}