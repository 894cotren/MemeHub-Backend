package com.voracityrat.memehubbackend.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举类
 *
 * @author grey
 */

@AllArgsConstructor
@Getter
public enum UserRoleEnum {

    COMMON_USER("用户", "user"),
    VIP_USER("会员", "vip"),
    ADMIN_USER("管理员", "admin");


    private final String text;
    private final String value;

    /**
     * 根据角色编码获取对应中文角色称谓
     *
     * @param value
     * @return
     */
    public static String getRoleTextByValue(String value) {

        if (value == null || value.isEmpty()) {
            return null;
        }

        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.value.equals(value)) {
                return userRoleEnum.text;
            }
        }
        return null;
    }

}
