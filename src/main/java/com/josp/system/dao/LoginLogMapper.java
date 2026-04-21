package com.josp.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.josp.system.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 登录日志 Mapper 接口
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 根据用户名查询登录日志
     */
    @Select("SELECT * FROM sys_login_log WHERE username = #{username} ORDER BY login_time DESC")
    java.util.List<LoginLog> selectByUsername(@Param("username") String username);

    /**
     * 根据用户ID查询登录日志
     */
    @Select("SELECT * FROM sys_login_log WHERE user_id = #{userId} ORDER BY login_time DESC")
    java.util.List<LoginLog> selectByUserId(@Param("userId") Long userId);
}
