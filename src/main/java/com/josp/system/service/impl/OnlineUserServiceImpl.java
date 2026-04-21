package com.josp.system.service.impl;

import com.josp.system.common.api.PageResult;
import com.josp.system.common.utils.PageUtils;
import com.josp.system.entity.OnlineUser;
import com.josp.system.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 在线用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineUserServiceImpl implements OnlineUserService {

    private static final String ONLINE_USER_KEY_PREFIX = "online:user:";
    private static final String ONLINE_USER_SET_KEY = "online:users";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageResult<OnlineUser> pageOnlineUsers(int pageNum, int pageSize, String username) {
        Set<Object> tokens = redisTemplate.opsForSet().members(ONLINE_USER_SET_KEY);
        if (tokens == null || tokens.isEmpty()) {
            return PageUtils.emptyPageResult();
        }

        List<OnlineUser> onlineUsers = new ArrayList<>();
        for (Object tokenObj : tokens) {
            String token = (String) tokenObj;
            String key = ONLINE_USER_KEY_PREFIX + token;
            OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(key);
            if (onlineUser != null) {
                if (username == null || username.isEmpty() || 
                    onlineUser.getUsername().toLowerCase().contains(username.toLowerCase())) {
                    onlineUsers.add(onlineUser);
                }
            } else {
                // 清理无效的token
                redisTemplate.opsForSet().remove(ONLINE_USER_SET_KEY, token);
            }
        }

        // 按登录时间倒序
        onlineUsers.sort((a, b) -> b.getLoginTime().compareTo(a.getLoginTime()));

        // 分页
        int total = onlineUsers.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<OnlineUser> pageList = start < total ? onlineUsers.subList(start, end) : Collections.emptyList();

        return PageUtils.buildPageResult(pageList, total);
    }

    @Override
    public OnlineUser getOnlineUserByToken(String token) {
        String key = ONLINE_USER_KEY_PREFIX + token;
        return (OnlineUser) redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean forceLogout(String token) {
        String key = ONLINE_USER_KEY_PREFIX + token;
        Boolean deleted = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            redisTemplate.opsForSet().remove(ONLINE_USER_SET_KEY, token);
            log.info("用户被强制下线, token: {}", token);
            return true;
        }
        return false;
    }

    @Override
    public boolean forceLogoutAll() {
        Set<Object> tokens = redisTemplate.opsForSet().members(ONLINE_USER_SET_KEY);
        if (tokens == null || tokens.isEmpty()) {
            return true;
        }
        for (Object tokenObj : tokens) {
            String token = (String) tokenObj;
            String key = ONLINE_USER_KEY_PREFIX + token;
            redisTemplate.delete(key);
        }
        redisTemplate.delete(ONLINE_USER_SET_KEY);
        log.info("所有在线用户已被强制下线");
        return true;
    }

    @Override
    public boolean refreshExpireTime(String token) {
        String key = ONLINE_USER_KEY_PREFIX + token;
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(key);
        if (onlineUser != null && onlineUser.getExpireTime() != null) {
            long ttl = Duration.between(LocalDateTime.now(), onlineUser.getExpireTime()).toSeconds();
            if (ttl > 0) {
                redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
                return true;
            }
        }
        return false;
    }

    @Override
    public long getOnlineUserCount() {
        Long size = redisTemplate.opsForSet().size(ONLINE_USER_SET_KEY);
        return size == null ? 0 : size;
    }

    @Override
    public void saveOnlineUser(OnlineUser onlineUser) {
        String key = ONLINE_USER_KEY_PREFIX + onlineUser.getToken();
        if (onlineUser.getExpireTime() != null) {
            long ttl = Duration.between(LocalDateTime.now(), onlineUser.getExpireTime()).toSeconds();
            if (ttl > 0) {
                redisTemplate.opsForValue().set(key, onlineUser, ttl, TimeUnit.SECONDS);
                redisTemplate.opsForSet().add(ONLINE_USER_SET_KEY, onlineUser.getToken());
            }
        } else {
            redisTemplate.opsForValue().set(key, onlineUser);
            redisTemplate.opsForSet().add(ONLINE_USER_SET_KEY, onlineUser.getToken());
        }
    }
}
