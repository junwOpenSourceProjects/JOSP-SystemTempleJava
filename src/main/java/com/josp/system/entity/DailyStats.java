package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_stats")
public class DailyStats {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private LocalDate statDate;
    private Integer totalUsers;
    private Integer totalOrders;
    private BigDecimal totalRevenue;
    private Integer todayVisits;
    private Integer yesterdayVisits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
