package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Weekly Website Visits Entity.
 *
 * <p>Daily visit and page-view breakdown for the past 7 days,
 * used in the dashboard weekly trend line chart.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("weekly_visits")
public class WeeklyVisits {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** Day of the week (e.g. "Monday", "Tuesday") */
    private String weekDay;

    /** Number of unique visits on this day */
    private Integer visits;

    /** Total page views on this day (a user may generate multiple views) */
    private Integer pageViews;

    /** ISO week identifier (e.g. "2026-W16") */
    private String statWeek;
}
