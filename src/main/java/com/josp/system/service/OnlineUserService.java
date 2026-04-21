package com.josp.system.service;

import com.josp.system.common.api.PageResult;
import com.josp.system.entity.OnlineUser;

/**
 * 在线用户服务接口
 */
public interface OnlineUserService {

    /**
     * 分页查询在线用户
     */
    PageResult<OnlineUser> pageOnlineUsers(int pageNum, int pageSize, String username);

    /**
     * 根据token获取在线用户信息
     */
    OnlineUser getOnlineUserByToken(String token);

    /**
     * 强制下线用户
     */
    boolean forceLogout(String token);

    /**
     * 强制下线所有用户
     */
    boolean forceLogoutAll();

    /**
     * 刷新在线用户过期时间
     */
    boolean refreshExpireTime(String token);

    /**
     * 获取当前在线用户数
     */
    long getOnlineUserCount();

    /**
     * 保存在线用户到Redis
     */
    void saveOnlineUser(OnlineUser onlineUser);
}
