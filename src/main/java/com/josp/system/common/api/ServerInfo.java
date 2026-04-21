package com.josp.system.common.api;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务器监控信息
 */
@Data
public class ServerInfo implements Serializable {

    /**
     * 服务器名称
     */
    private String serverName;

    /**
     * 操作系统
     */
    private String osName;

    /**
     * 服务器IP
     */
    private String serverIp;

    /**
     * 操作系统架构
     */
    private String osArch;

    /**
     * 系统版本
     */
    private String osVersion;

    /**
     * CPU核心数
     */
    private Integer cpuCoreCount;

    /**
     * CPU使用率
     */
    private Double cpuUsage;

    /**
     * 内存总量(MB)
     */
    private Long totalMemory;

    /**
     * 内存使用量(MB)
     */
    private Long usedMemory;

    /**
     * 内存使用率
     */
    private Double memoryUsage;

    /**
     * Java虚拟机版本
     */
    private String javaVersion;

    /**
     * 运行时间(秒)
     */
    private Long uptime;

    /**
     * 磁盘总量(G)
     */
    private Long totalDisk;

    /**
     * 磁盘使用量(G)
     */
    private Long usedDisk;

    /**
     * 磁盘使用率
     */
    private Double diskUsage;
}
