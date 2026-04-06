package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("category_sales")
public class CategorySales {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String categoryName;
    private Integer salesValue;
    private LocalDateTime createdAt;
}
