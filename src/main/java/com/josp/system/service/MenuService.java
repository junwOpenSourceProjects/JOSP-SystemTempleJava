package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.josp.system.common.api.RouteVO;
import com.josp.system.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    
    /**
     * 获取当前用户的动态路由列表
     */
    List<RouteVO> listRoutes();
}
