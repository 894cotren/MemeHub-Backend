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
    favorite_limit int          default 500      not null comment '可收藏上限',
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
    origin_url   varchar(512)                       null comment '源图片 url',
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
    review_status int   default 0                   not null comment '图片状态 0-待审核 1-审核通过 2-审核驳回 3-违规下架',
    review_message varchar(512)                     null comment '审核备注',
    reviewer_id  bigint                             null comment '审核人 ID',
    review_time  datetime                           null comment '审核时间',
    space_id     bigint                             null comment '空间 id（为空表示公共空间）',
    user_id      bigint                             not null comment '创建用户 id',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint  default 0                 not null comment '是否删除',
    INDEX idx_introduction (introduction),
    INDEX idx_category (category),
    INDEX idx_tags (tags),
    INDEX idx_user_id (user_id),
    INDEX idx_reviewStatus (review_status),
    INDEX idx_space_id (space_id)
) comment '图片表' collate = utf8mb4_unicode_ci;


-- 用户收藏表
create table if not exists user_picture
(
    id          bigint auto_increment comment 'id' primary key,
    user_id     bigint                             not null comment '用户 id',
    pic_id      bigint                             not null comment '图片 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    INDEX idx_user_id (user_id, pic_id)
) comment '用户收藏图片-收藏表' collate = utf8mb4_unicode_ci;



-- 空间表
CREATE TABLE IF NOT EXISTS space
(
    id           BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    space_name   VARCHAR(128)                       NULL COMMENT '空间名称',
    space_level  INT      DEFAULT 0                 NULL COMMENT '空间级别：0-普通版 1-专业版 2-旗舰版',
    max_size     BIGINT   DEFAULT 0                 NULL COMMENT '空间图片的最大总大小',
    max_count    BIGINT   DEFAULT 0                 NULL COMMENT '空间图片的最大数量',
    total_size   BIGINT   DEFAULT 0                 NULL COMMENT '当前空间下图片的总大小',
    total_count  BIGINT   DEFAULT 0                 NULL COMMENT '当前空间下的图片数量',
    user_id      BIGINT                             NOT NULL COMMENT '创建用户 id',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    edit_time    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '编辑时间',
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete    TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    -- 索引设计
    INDEX idx_user_id (user_id),        -- 提升基于用户的查询效率
    INDEX idx_space_name (space_name),  -- 提升基于空间名称的查询效率
    INDEX idx_space_level (space_level) -- 提升按空间级别查询的效率
) COMMENT '空间' COLLATE = utf8mb4_unicode_ci;


# -- 添加新列
# ALTER TABLE picture
#     ADD COLUMN space_id  bigint  null comment '空间 id（为空表示公共空间）';
#
# -- 创建索引
# CREATE INDEX idx_space_id ON picture (space_id);


ALTER TABLE space
    ADD COLUMN space_type int default 0 not null comment '空间类型：0-私有 1-团队';

CREATE INDEX idx_spaceType ON space (space_type);



CREATE TABLE IF NOT EXISTS space_user
(
    id           BIGINT AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    space_id     BIGINT                             NOT NULL COMMENT '空间ID',
    user_id      BIGINT                             NOT NULL COMMENT '用户ID',
    space_role   VARCHAR(128) DEFAULT 'viewer'      NULL COMMENT '空间角色：viewer/editor/admin',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 索引设计
    UNIQUE KEY uk_space_id_user_id (space_id, user_id), -- 唯一索引，用户在一个空间中只能有一个角色
    INDEX idx_space_id (space_id),                    -- 提升按空间查询的性能
    INDEX idx_user_id (user_id)                       -- 提升按用户查询的性能
) COMMENT '空间用户关联' COLLATE = utf8mb4_unicode_ci;

