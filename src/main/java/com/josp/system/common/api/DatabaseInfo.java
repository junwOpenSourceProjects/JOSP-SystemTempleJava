package com.josp.system.common.api;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据库连接信息
 */
@Data
public class DatabaseInfo implements Serializable {

    /**
     * 数据库类型
     */
    private String databaseType;

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * 连接URL
     */
    private String url;

    /**
     * 驱动名称
     */
    private String driverName;

    /**
     * 数据库版本
     */
    private String version;

    /**
     * 当前连接数
     */
    private Integer connectionCount;

    /**
     * 活跃连接数
     */
    private Integer activeConnection;

    /**
     * 空闲连接数
     */
    private Integer idleConnection;

    /**
     * 连接池最大连接数
     */
    private Integer maxConnections;

    /**
     * 连接池最小连接数
     */
    private Integer minConnections;

    /**
     * 连接状态
     */
    private String status;
}
