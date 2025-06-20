package com.voracityrat.memehubbackend.model.dto.user;

import com.voracityrat.memehubbackend.model.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 分页查询用户request
 *
 * @author grey
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageListRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户角色：user/vip/admin 其他权益待定
     */
    private String userRole;

    /**
     * 会员编号
     */
    private String vipNumber;


}
