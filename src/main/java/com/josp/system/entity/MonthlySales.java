package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("monthly_sales")
public class MonthlySales {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String monthName;
    private Integer sales;
    private Integer target;
    private Integer yearVal;
}
