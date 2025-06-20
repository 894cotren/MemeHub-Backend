create database if not exists 'memehub';

use memehub;

-- 用户表
create table if not exists user
(
    id              bigint auto_increment comment 'id' primary key,
    user_account    varchar(256)                           not null comment '账号',
    user_password   varchar(512)                           not null comment '密码',
    user_name      varchar(256) default '新用户' null comment '用户昵称',
    user_avatar     varchar(1024)                          null comment '用户头像',
    user_profile    varchar(512)                           null comment '用户简介',
    user_email      varchar(256)                           null comment '用户邮箱',
    user_role       varchar(256) default 'user'            not null comment '用户角色：user/vip/admin 其他权益待定',
    vip_number     varchar(256)                  null comment '会员编号',
    favorite_count  int          default 0                 not null comment '收藏数',
    favorite_limit int          default 200      not null comment '可收藏上限',
    vip_expire_time datetime                               null comment '会员过期时间',
    edit_time       datetime     default CURRENT_TIMESTAMP not null comment '编辑时间 （业务更新）',
    create_time     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_user_account (user_account),
    INDEX idx_user_name (user_name)
) comment '用户表' collate = utf8mb4_unicode_ci;
