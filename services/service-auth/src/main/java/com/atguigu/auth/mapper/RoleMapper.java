package com.atguigu.auth.mapper;

import com.atguigu.auth.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<String> findRoleCodeByUserId(@Param("userId") Long userId);
}