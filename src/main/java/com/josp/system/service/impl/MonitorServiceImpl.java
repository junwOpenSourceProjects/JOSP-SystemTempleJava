package com.josp.system.service.impl;

import com.josp.system.common.api.DatabaseInfo;
import com.josp.system.common.api.RedisInfo;
import com.josp.system.common.api.ServerInfo;
import com.josp.system.service.MonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Properties;

/**
 * 系统监控服务实现类
 */
@Slf4j
@Service
@Profile("!test")
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public ServerInfo getServerInfo() {
        ServerInfo serverInfo = new ServerInfo();
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

            serverInfo.setOsName(System.getProperty("os.name"));
            serverInfo.setOsArch(System.getProperty("os.arch"));
            serverInfo.setOsVersion(System.getProperty("os.version"));
            serverInfo.setServerIp(InetAddress.getLocalHost().getHostAddress());
            serverInfo.setServerName(InetAddress.getLocalHost().getHostName());
            serverInfo.setJavaVersion(System.getProperty("java.version"));
            serverInfo.setUptime(runtimeBean.getUptime() / 1000);

            // CPU核心数
            serverInfo.setCpuCoreCount(osBean.getAvailableProcessors());

            // 内存信息
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory() / (1024 * 1024);
            long usedMemory = totalMemory - runtime.freeMemory() / (1024 * 1024);
            serverInfo.setTotalMemory(totalMemory);
            serverInfo.setUsedMemory(usedMemory);
            if (totalMemory > 0) {
                serverInfo.setMemoryUsage((double) usedMemory / totalMemory * 100);
            }

            // 磁盘信息 (简化)
            serverInfo.setTotalDisk(500L); // 需要文件系统API获取
            serverInfo.setUsedDisk(200L);
            serverInfo.setDiskUsage(40.0);

            // CPU使用率 (简化)
            serverInfo.setCpuUsage(0.0);

        } catch (Exception e) {
            log.error("获取服务器信息失败", e);
        }
        return serverInfo;
    }

    @Override
    @SuppressWarnings("deprecation")
    public DatabaseInfo getDatabaseInfo() {
        DatabaseInfo dbInfo = new DatabaseInfo();
        dbInfo.setDatabaseType("MySQL");
        dbInfo.setStatus("UP");

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            dbInfo.setDatabaseName(connection.getCatalog()); // MySQL中catalog即database名称
            dbInfo.setUrl(metaData.getURL());
            dbInfo.setDriverName(metaData.getDriverName());
            dbInfo.setVersion(metaData.getDatabaseProductVersion());

            // 连接池信息需要从数据源获取，这里设置默认值
            dbInfo.setConnectionCount(0);
            dbInfo.setActiveConnection(0);
            dbInfo.setIdleConnection(0);
            dbInfo.setMaxConnections(0);
            dbInfo.setMinConnections(0);

        } catch (Exception e) {
            log.error("获取数据库信息失败", e);
            dbInfo.setStatus("DOWN");
        }
        return dbInfo;
    }

    @Override
    public RedisInfo getRedisInfo() {
        RedisInfo redisInfo = new RedisInfo();
        redisInfo.setStatus("UP");

        try {
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
            try {
                Properties info = connection.serverCommands().info();
                if (info != null) {
                    redisInfo.setVersion(info.getProperty("redis_version", "unknown"));
                    redisInfo.setMode(info.getProperty("redis_mode", "standalone"));
                    redisInfo.setPort(6379);

                    String clients = info.getProperty("connected_clients", "0");
                    redisInfo.setConnectedClients(Long.parseLong(clients));

                    String uptime = info.getProperty("uptime_in_seconds", "0");
                    redisInfo.setUptime(Long.parseLong(uptime));

                    String usedMemory = info.getProperty("used_memory_human", "0");
                    redisInfo.setUsedMemory(usedMemory);

                    String usedMemoryPeak = info.getProperty("used_memory_peak_human", "0");
                    redisInfo.setUsedMemoryPeak(usedMemoryPeak);

                    String keyspaceHitrate = info.getProperty("keyspace_hitrate", "0");
                    redisInfo.setKeyspaceHitrate(Double.parseDouble(keyspaceHitrate));

                    // 获取键数量
                    Long dbSize = connection.dbSize();
                    redisInfo.setKeyCount(dbSize != null ? dbSize : 0);

                    // CPU使用率 (简化)
                    redisInfo.setCpuUsage(0.0);

                    // 内存使用率 (简化)
                    redisInfo.setMemoryUsage(0.0);
                }
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            log.error("获取Redis信息失败", e);
            redisInfo.setStatus("DOWN");
        }
        return redisInfo;
    }
}
