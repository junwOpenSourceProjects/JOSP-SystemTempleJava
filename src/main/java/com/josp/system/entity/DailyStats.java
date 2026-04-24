package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Daily System Statistics Entity.
 *
 * <p>Aggregated daily metrics for the dashboard overview.
 * Each record represents one calendar date's statistics.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("daily_stats")
public class DailyStats {
    /** Primary key, snowflake ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** The calendar date this record describes */
    private LocalDate statDate;

    /** Total cumulative registered users as of this date */
    private Integer totalUsers;

    /** Total cumulative orders placed as of this date */
    private Integer totalOrders;

    /** Total cumulative revenue (yuan) as of this date */
    private BigDecimal totalRevenue;

    /** Page visits recorded on this specific date */
    private Integer todayVisits;

    /** Page visits recorded on the previous date (used for growth rate calculation) */
    private Integer yesterdayVisits;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
