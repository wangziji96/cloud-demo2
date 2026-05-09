package com.atguigu.auth.mapper;

import com.atguigu.auth.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 继承BaseMapper后自动拥有CRUD方法
}