package com.voracityrat.memehubbackend.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录后的用户脱敏数据
 *
 * @TableName user
 */
@Data
public class LoginUserVo implements Serializable {
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
     * 用户角色：user/vip/admin 其他权益待定
     */
    private String userRole;

    /**
     * 会员编号
     */
    private Long vipNumber;

    /**
     * 收藏数
     */
    private Integer favoriteCount;

    /**
     * 会员过期时间
     */
    private Date vipExpireTime;

    /**
     * 编辑时间 （业务更新）
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    private static final long serialVersionUID = 1L;
}
