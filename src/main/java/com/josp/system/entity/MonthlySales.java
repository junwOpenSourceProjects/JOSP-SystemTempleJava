package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Monthly Sales Target Entity.
 *
 * <p>Actual vs. target monthly sales figures used in the dashboard bar chart.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("monthly_sales")
public class MonthlySales {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** Display name of the month (e.g. "January", "Feb") */
    private String monthName;

    /** Actual sales figure for this month */
    private Integer sales;

    /** Target sales figure for this month */
    private Integer target;

    /** The year this record belongs to (e.g. 2026) */
    private Integer yearVal;
}
