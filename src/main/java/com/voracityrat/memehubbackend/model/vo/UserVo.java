package com.voracityrat.memehubbackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 脱敏用户数据，初始创建为了给分页使用
 *
 * @TableName user
 */
@Data
public class UserVo implements Serializable {
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
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户角色：user/admin 其他权益待定
     */
    private String userRole;

    /**
     * 收藏数
     */
    private Integer favoriteCount;

    /**
     * 可收藏上限
     */
    private Integer favoriteLimit;

    /**
     * 创建时间
     */
    private Date createTime;


    private static final long serialVersionUID = 1L;
}
