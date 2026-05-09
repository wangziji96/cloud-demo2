package com.atguigu.auth.service.impl;

import com.atguigu.auth.Role;
import com.atguigu.auth.mapper.RoleMapper;
import com.atguigu.auth.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    // 继承ServiceImpl后自动实现基本CRUD方法，可根据需要添加自定义业务逻辑
}