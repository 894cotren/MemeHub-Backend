package com.voracityrat.memehubbackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册接参类
 *
 * @author grey
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
