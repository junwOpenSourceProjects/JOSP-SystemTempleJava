package com.josp.system.service.impl;

import com.josp.system.common.api.PageResult;
import com.josp.system.entity.OnlineUser;
import com.josp.system.service.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 在线用户服务测试桩实现（test profile 专用）
 * <p>生产环境由 {@link OnlineUserServiceImpl} 提供真实 Redis 实现。
 */
@Slf4j
@Service
@Profile("test")
public class OnlineUserServiceTestImpl implements OnlineUserService {

    @Override
    public PageResult<OnlineUser> pageOnlineUsers(int pageNum, int pageSize, String username) {
        return new PageResult<>(Collections.emptyList());
    }

    @Override
    public OnlineUser getOnlineUserByToken(String token) {
        return null;
    }

    @Override
    public boolean forceLogout(String token) {
        return true;
    }

    @Override
    public boolean forceLogoutAll() {
        return true;
    }

    @Override
    public boolean refreshExpireTime(String token) {
        return true;
    }

    @Override
    public long getOnlineUserCount() {
        return 0;
    }

    @Override
    public void saveOnlineUser(OnlineUser onlineUser) {
        // no-op: test 环境不真实存储
    }
}
