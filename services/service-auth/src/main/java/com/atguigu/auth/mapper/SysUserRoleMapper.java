package com.atguigu.auth.mapper;


import com.atguigu.auth.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    List<String> findRoleCodeByUserId(@Param("userId") Long userId);
}
