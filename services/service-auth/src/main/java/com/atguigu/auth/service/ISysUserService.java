package com.atguigu.auth.service;

import com.atguigu.auth.SysUser;
import com.atguigu.auth.User;
import com.atguigu.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ISysUserService extends IService<SysUser> {
    /**
     *  注册
     * @param user
     */
    void register(SysUser user);

    /**
     * 登录
     * @param user
     * @return
     */
    Result login(User user);
}
