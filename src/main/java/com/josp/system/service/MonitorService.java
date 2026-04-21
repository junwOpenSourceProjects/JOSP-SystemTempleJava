package com.josp.system.service;

import com.josp.system.common.api.DatabaseInfo;
import com.josp.system.common.api.RedisInfo;
import com.josp.system.common.api.ServerInfo;

/**
 * 系统监控服务接口
 */
public interface MonitorService {

    /**
     * 获取服务器信息
     */
    ServerInfo getServerInfo();

    /**
     * 获取数据库连接信息
     */
    DatabaseInfo getDatabaseInfo();

    /**
     * 获取Redis状态信息
     */
    RedisInfo getRedisInfo();
}
