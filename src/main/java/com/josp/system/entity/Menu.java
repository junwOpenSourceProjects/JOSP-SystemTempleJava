package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class Menu implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long parentId;

    private String name;

    private Integer type;

    private String path;

    private String component;

    private String icon;

    private Integer sort;

    private Integer visible;

    private String redirect;

    private String perm;

    private Integer keepAlive;

    private Integer alwaysShow;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
