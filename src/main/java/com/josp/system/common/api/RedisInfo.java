package com.josp.system.common.api;

import lombok.Data;

import java.io.Serializable;

/**
 * Redis缓存信息
 */
@Data
public class RedisInfo implements Serializable {

    /**
     * Redis版本
     */
    private String version;

    /**
     * 运行模式：standalone-单机，cluster-集群，sentinel-哨兵
     */
    private String mode;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 客户端数量
     */
    private Long connectedClients;

    /**
     * 运行时间(秒)
     */
    private Long uptime;

    /**
     * 使用内存
     */
    private String usedMemory;

    /**
     * 使用内存峰值
     */
    private String usedMemoryPeak;

    /**
     * 内存使用率
     */
    private Double memoryUsage;

    /**
     * CPU使用率
     */
    private Double cpuUsage;

    /**
     * 命中率
     */
    private Double keyspaceHitrate;

    /**
     * 键数量
     */
    private Long keyCount;

    /**
     * 状态：UP-正常，DOWN-异常
     */
    private String status;
}
