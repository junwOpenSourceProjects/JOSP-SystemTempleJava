package com.josp.system.service.impl;

import com.josp.system.common.api.DatabaseInfo;
import com.josp.system.common.api.RedisInfo;
import com.josp.system.common.api.ServerInfo;
import com.josp.system.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 系统监控服务测试桩实现（test profile 专用）
 * <p>生产环境由 {@link MonitorServiceImpl} 提供真实的服务器/数据库/Redis 信息。
 */
@Slf4j
@Service
@Profile("test")
public class MonitorServiceTestImpl implements MonitorService {

    @Override
    public ServerInfo getServerInfo() {
        ServerInfo info = new ServerInfo();
        info.setServerName("test-server");
        info.setOsName("Test OS");
        info.setServerIp("127.0.0.1");
        info.setOsArch("amd64");
        info.setOsVersion("1.0");
        info.setCpuCoreCount(4);
        info.setCpuUsage(0.0);
        info.setTotalMemory(8192L);
        info.setUsedMemory(2048L);
        info.setMemoryUsage(25.0);
        info.setJavaVersion(System.getProperty("java.version"));
        info.setUptime(0L);
        info.setTotalDisk(256L);
        info.setUsedDisk(128L);
        info.setDiskUsage(50.0);
        return info;
    }

    @Override
    public DatabaseInfo getDatabaseInfo() {
        DatabaseInfo info = new DatabaseInfo();
        info.setDatabaseType("H2");
        info.setDatabaseName("testdb");
        info.setUrl("jdbc:h2:mem:testdb");
        info.setDriverName("org.h2.Driver");
        info.setVersion("2.2.224");
        info.setConnectionCount(1);
        info.setActiveConnection(0);
        info.setIdleConnection(1);
        info.setMaxConnections(10);
        info.setMinConnections(1);
        info.setStatus("UP");
        return info;
    }

    @Override
    public RedisInfo getRedisInfo() {
        RedisInfo info = new RedisInfo();
        info.setStatus("DOWN");
        info.setVersion("N/A");
        info.setMode("standalone");
        info.setPort(6379);
        info.setConnectedClients(0L);
        info.setUptime(0L);
        info.setUsedMemory("0B");
        info.setUsedMemoryPeak("0B");
        info.setMemoryUsage(0.0);
        info.setCpuUsage(0.0);
        info.setKeyspaceHitrate(0.0);
        info.setKeyCount(0L);
        return info;
    }
}
