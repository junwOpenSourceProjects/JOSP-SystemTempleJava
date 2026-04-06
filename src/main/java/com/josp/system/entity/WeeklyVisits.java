package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("weekly_visits")
public class WeeklyVisits {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String weekDay;
    private Integer visits;
    private Integer pageViews;
    private String statWeek;
}
