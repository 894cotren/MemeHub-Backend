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

    /**
     * 根据角色编码获取到对应角色的枚举类
     * @param value
     * @return
     */
    public static UserRoleEnum getRoleEnumByValue(String value) {

        if (value == null || value.isEmpty()) {
            return null;
        }

        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.value.equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }


}
