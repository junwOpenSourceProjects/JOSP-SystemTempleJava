package com.josp.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.josp.system.entity.OperLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 操作日志 Mapper 接口
 */
@Mapper
public interface OperLogMapper extends BaseMapper<OperLog> {

    /**
     * 根据操作人名称查询操作日志
     */
    @Select("<script>SELECT * FROM sys_oper_log <where> <if test='operName != null and operName != \"\"'> AND oper_name LIKE CONCAT('%', #{operName}, '%') </if> </where> ORDER BY oper_time DESC</script>")
    java.util.List<OperLog> selectByOperName(@Param("operName") String operName);
}
