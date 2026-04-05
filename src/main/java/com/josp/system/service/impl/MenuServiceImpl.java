package com.josp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.common.api.Meta;
import com.josp.system.common.api.RouteVO;
import com.josp.system.dao.MenuMapper;
import com.josp.system.entity.Menu;
import com.josp.system.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<RouteVO> listRoutes() {
        List<Menu> menus = baseMapper.listAllVisibleMenus();
        return buildRouteTree(menus, 0L);
    }

    private List<RouteVO> buildRouteTree(List<Menu> menus, Long parentId) {
        if (CollectionUtils.isEmpty(menus)) {
            return new ArrayList<>();
        }

        return menus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .map(menu -> {
                    RouteVO routeVO = new RouteVO();
                    routeVO.setName(menu.getName());
                    routeVO.setPath(menu.getPath());
                    routeVO.setComponent(menu.getComponent());
                    routeVO.setRedirect(menu.getRedirect());

                    Meta meta = Meta.builder()
                            .title(menu.getName())
                            .icon(menu.getIcon())
                            .hidden(Objects.equals(menu.getVisible(), 0))
                            .keepAlive(Objects.equals(menu.getKeepAlive(), 1))
                            .alwaysShow(Objects.equals(menu.getAlwaysShow(), 1))
                            .build();
                    routeVO.setMeta(meta);

                    // 递归获取子菜单
                    List<RouteVO> children = buildRouteTree(menus, menu.getId());
                    if (!CollectionUtils.isEmpty(children)) {
                        routeVO.setChildren(children);
                    }
                    return routeVO;
                })
                .collect(Collectors.toList());
    }
}
