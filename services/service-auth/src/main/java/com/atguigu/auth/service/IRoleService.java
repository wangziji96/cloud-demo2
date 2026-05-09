package com.atguigu.auth.service;

import com.atguigu.auth.Role;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IRoleService extends IService<Role> {
    // 继承IService后自动拥有基本CRUD方法，可根据需要添加自定义业务方法
}