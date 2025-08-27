package com.voracityrat.memehubbackend.spaceauthcheck;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.voracityrat.memehubbackend.service.SpaceUserService;
import com.voracityrat.memehubbackend.service.UserService;
import com.voracityrat.memehubbackend.spaceauthcheck.model.SpaceUserAuthConfig;
import com.voracityrat.memehubbackend.spaceauthcheck.model.SpaceUserRole;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//提供空间鉴权方法的类。
@Component
public class SpaceUserAuthManager {

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private UserService userService;

    //读取配置文件的常量
    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    //静态代码块跟类一起加载我们配置的权限文件
    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据角色获取权限列表
     */
    public List<String> getPermissionsByRole(String spaceUserRole) {
        if (StrUtil.isBlank(spaceUserRole)) {
            return new ArrayList<>();
        }
        // 找到匹配的角色
        SpaceUserRole role = SPACE_USER_AUTH_CONFIG.getRoles().stream()
                .filter(r -> spaceUserRole.equals(r.getKey()))
                .findFirst()
                .orElse(null);
        if (role == null) {
            return new ArrayList<>();
        }
        return role.getPermissions();
    }

    //根据角色，目标权限，返回是或者否有权限。
    public boolean checkSpaceAuth(String spaceUserRole,String targetAuth){
        List<String> permissionsByRole = this.getPermissionsByRole(spaceUserRole);
        for (String permissions : permissionsByRole) {
            if (permissions.equals(targetAuth)){
                return true;
            }
        }
        return false;
    }
}

