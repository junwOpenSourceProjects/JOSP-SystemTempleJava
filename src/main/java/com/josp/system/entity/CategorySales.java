package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Product Category Sales Entity.
 *
 * <p>Sales figures broken down by product category for the dashboard pie chart.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("category_sales")
public class CategorySales {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** Name of the product category (e.g. "Electronics", "Clothing") */
    private String categoryName;

    /** Sales amount/value for this category */
    private Integer salesValue;

    private LocalDateTime createdAt;
}
