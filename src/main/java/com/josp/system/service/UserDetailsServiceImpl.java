package com.josp.system.service;

import com.josp.system.dao.LoginUserMapper;
import com.josp.system.dao.AccountRoleMapper;
import com.josp.system.entity.LoginUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring Security UserDetailsService for user authentication.
 * This service loads user details from the database during authentication.
 *
 * @author JOSP System
 * @version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final LoginUserMapper loginUserMapper;
    private final AccountRoleMapper accountRoleMapper;

    /**
     * Constructor for dependency injection.
     *
     * @param loginUserMapper  mapper for login_user table operations
     * @param accountRoleMapper mapper for account_role table operations
     */
    public UserDetailsServiceImpl(LoginUserMapper loginUserMapper, AccountRoleMapper accountRoleMapper) {
        this.loginUserMapper = loginUserMapper;
        this.accountRoleMapper = accountRoleMapper;
    }

    /**
     * Loads user details by username for Spring Security authentication.
     * This method is called during authentication to retrieve user information.
     *
     * @param username the username to search for
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user is not found
     * @throws DisabledException if user account is disabled (status = 0)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUser user = loginUserMapper.selectOne(new LambdaQueryWrapper<LoginUser>()
                .eq(LoginUser::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("Username or password is incorrect");
        }

        // Check if user account is disabled (status = 0 means disabled, status = 1 means enabled)
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new DisabledException("User account is disabled");
        }

        // Load roles for this user and set as authorities
        var roleCodes = accountRoleMapper.selectRoleCodesByUserId(user.getId());
        user.setRoles(roleCodes);

        return user;
    }
}
