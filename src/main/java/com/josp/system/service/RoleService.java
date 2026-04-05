package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.dao.RoleMapper;
import com.josp.system.entity.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {
}
