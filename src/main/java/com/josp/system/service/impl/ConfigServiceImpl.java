package com.josp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.common.api.PageResult;
import com.josp.system.dao.ConfigMapper;
import com.josp.system.entity.Config;
import com.josp.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    /** 本地缓存：key -> value */
    private static final Map<String, String> CONFIG_CACHE = new ConcurrentHashMap<>();

    @Override
    public PageResult<Config> pageConfigs(Integer page, Integer limit, String configName, String configKey) {
        Page<Config> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        if (configName != null && !configName.isEmpty()) {
            wrapper.like(Config::getName, configName);
        }
        if (configKey != null && !configKey.isEmpty()) {
            wrapper.like(Config::getConfigKey, configKey);
        }
        wrapper.orderByDesc(Config::getCreateTime);

        IPage<Config> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Override
    public String getValueByKey(String configKey) {
        // 先从缓存取
        if (CONFIG_CACHE.containsKey(configKey)) {
            return CONFIG_CACHE.get(configKey);
        }
        // 缓存未命中，从数据库取
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, configKey);
        wrapper.eq(Config::getStatus, 1);
        wrapper.last("LIMIT 1");
        Config config = getOne(wrapper);
        if (config != null) {
            CONFIG_CACHE.put(configKey, config.getValue());
            return config.getValue();
        }
        return null;
    }

    @Override
    public void refreshCache() {
        log.info("刷新系统配置缓存");
        CONFIG_CACHE.clear();
        List<Config> configs = list();
        for (Config config : configs) {
            if (config.getStatus() != null && config.getStatus() == 1) {
                CONFIG_CACHE.put(config.getConfigKey(), config.getValue());
            }
        }
        log.info("配置缓存刷新完成，共加载 {} 条配置", CONFIG_CACHE.size());
    }

    @Override
    public Map<String, String> getAllConfigMap() {
        Map<String, String> result = new HashMap<>();
        List<Config> configs = list();
        for (Config config : configs) {
            if (config.getStatus() != null && config.getStatus() == 1) {
                result.put(config.getConfigKey(), config.getValue());
            }
        }
        return result;
    }
}
