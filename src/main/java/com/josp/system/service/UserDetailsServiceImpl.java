package com.josp.system.service;

import com.josp.system.dao.LoginUserMapper;
import com.josp.system.dao.AccountRoleMapper;
import com.josp.system.entity.LoginUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final LoginUserMapper loginUserMapper;
    private final AccountRoleMapper accountRoleMapper;

    public UserDetailsServiceImpl(LoginUserMapper loginUserMapper, AccountRoleMapper accountRoleMapper) {
        this.loginUserMapper = loginUserMapper;
        this.accountRoleMapper = accountRoleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUser user = loginUserMapper.selectOne(new LambdaQueryWrapper<LoginUser>()
                .eq(LoginUser::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // Load roles for this user and set as authorities
        var roleCodes = accountRoleMapper.selectRoleCodesByUserId(user.getId());
        user.setRoles(roleCodes);

        return user;
    }
}
