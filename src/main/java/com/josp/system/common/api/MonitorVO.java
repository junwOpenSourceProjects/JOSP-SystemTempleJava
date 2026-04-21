package com.josp.system.common.api;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统监控VO
 */
@Data
public class MonitorVO implements Serializable {

    /**
     * 服务器信息
     */
    private ServerInfo server;

    /**
     * 数据库信息
     */
    private DatabaseInfo database;

    /**
     * Redis信息
     */
    private RedisInfo redis;

    /**
     * 监控时间
     */
    private Long timestamp;
}
