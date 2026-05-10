package com.atguigu.auth.service.impl;

import com.atguigu.auth.SysUser;
import com.atguigu.auth.User;
import com.atguigu.auth.mapper.RoleMapper;
import com.atguigu.auth.mapper.SysUserMapper;
import com.atguigu.auth.mapper.SysUserRoleMapper;
import com.atguigu.auth.service.ISysUserService;
import com.atguigu.result.Result;
import com.atguigu.utils.JwtUtil;
import com.atguigu.utils.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Override
    @Transactional
    public void register(SysUser user) {
        //1.检查用户名是否存在
        LambdaQueryWrapper<SysUser> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(SysUser::getUsername, user.getUsername());
        SysUser sysUser = userMapper.selectOne(userQueryWrapper);
        if (sysUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        //2.加密密码
        String hashPassword = PasswordUtil.encode(user.getPasswordHash());
        user.setPasswordHash(hashPassword);
        userMapper.insert(user);
    }

    @Override
    public Result login(User user) {
        //1.查询用户
        SysUser sysUser = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, user.getUsername()));
        //2.验证密码
        if (sysUser != null && !PasswordUtil.matches(user.getPassword(), sysUser.getPasswordHash())) {
            throw new RuntimeException("用户不存在或密码错误");
        }
        if (sysUser.getStatus() == 0) {
            throw new RuntimeException("用户被禁用");
        }
        //3.查询角色Id
        List<String> roleCodes = userRoleMapper.findRoleCodeByUserId(sysUser.getId());
        //4.生成jwt
        String token = JwtUtil.generateToken(sysUser.getId().toString(), roleCodes);
        return Result.success(token);
    }
}