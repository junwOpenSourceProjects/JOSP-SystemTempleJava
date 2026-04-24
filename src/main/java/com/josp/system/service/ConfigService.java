package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.josp.system.common.api.PageResult;
import com.josp.system.entity.Config;

import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface ConfigService extends IService<Config> {

    /**
     * 分页查询系统配置
     */
    PageResult<Config> pageConfigs(Integer page, Integer limit, String configName, String configKey);

    /**
     * 根据键名获取配置值
     */
    String getValueByKey(String configKey);

    /**
     * 刷新配置缓存
     */
    void refreshCache();

    /**
     * 获取所有配置（Map形式）
     */
    Map<String, String> getAllConfigMap();
}
