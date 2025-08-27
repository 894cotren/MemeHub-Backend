package com.voracityrat.memehubbackend.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建空间成员请求
 */
@Data
public class SpaceUserAddRequest implements Serializable {

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 用户 ID
     */
    private Long userId;
//
//    /**   不考虑用账号，我们直接输入就邀请了，要是我随便输入账号邀请呢？ 所以我还是前端用户信息页面增加一个用户可以查看自己ID的功能。
//     * 用户 账号
//     */
//    private String userAccount;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}