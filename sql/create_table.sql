create database if not exists 'memehub';

use memehub;

-- 用户表
create table if not exists user
(
    id              bigint auto_increment comment 'id' primary key,
    user_account    varchar(256)                           not null comment '账号',
    user_password   varchar(512)                           not null comment '密码',
    user_name      varchar(256)                            null comment '用户昵称',
    user_avatar     varchar(1024)                          null comment '用户头像',
    user_profile    varchar(512)                           null comment '用户简介',
    user_email      varchar(256)                           null comment '用户邮箱',
    user_role       varchar(256) default 'user'            not null comment '用户角色：user/admin 其他权益待定',
    favorite_count  int          default 0                 not null comment '收藏数',
    favorite_limit int          default 200      not null comment '可收藏上限',
    create_time     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_user_account (user_account),
    INDEX idx_user_name (user_name)
) comment '用户表' collate = utf8mb4_unicode_ci;


-- 图片表
create table if not exists picture
(
    id           bigint auto_increment comment 'id' primary key,
    pic_url      varchar(512)                       not null comment '图片 url',
    pic_name     varchar(128)                       null comment '图片名称',
    introduction varchar(512)                       null comment '简介',
    category     varchar(64)                        null comment '分类',
    tags         varchar(512)                       null comment '标签（JSON 数组）',
    pic_size     bigint                             null comment '图片体积',
    pic_width    int                                null comment '图片宽度',
    pic_height   int                                null comment '图片高度',
    pic_scale    double                             null comment '图片宽高比例',
    pic_format   varchar(32)                        null comment '图片格式',
    pic_status   tinyint  default 1                 not null comment '图片状态 0-审核通过 1-待审核 2-审核驳回 3-违规下架',
    user_id      bigint                             not null comment '创建用户 id',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint  default 0                 not null comment '是否删除',
    INDEX idx_introduction (introduction),
    INDEX idx_category (category),
    INDEX idx_tags (tags),
    INDEX idx_user_id (user_id)
) comment '图片表' collate = utf8mb4_unicode_ci;


