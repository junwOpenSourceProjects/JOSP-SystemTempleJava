package com.josp.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.josp.system.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 通知公告 Mapper 接口
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 增加浏览次数
     */
    @Update("UPDATE sys_notice SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
}
