# 创建 blog 数据库

```mysql
create schema blog collate utf8mb4_general_ci;
```

## 用户信息表

```mysql
create table sys_user
(
    id                         bigint unsigned              not null comment '主键'
        primary key,
    username                   varchar(50)                  not null comment '用户名',
    password                   varchar(255)                 not null comment '账号',
    is_account_non_expired     tinyint unsigned default '1' not null comment '账号是否未过期（0过期，1未过期）',
    is_account_non_locked      tinyint unsigned default '1' not null comment '账号是否未锁定（0锁定，1未锁定）',
    is_credentials_non_expired tinyint unsigned default '1' not null comment '凭证是否未过期（0过期，1未过期）',
    is_enabled                 tinyint unsigned default '1' not null comment '账号是否启用（1启用，0未启用）',
    is_deleted                 tinyint unsigned default '0' not null comment '是否删除（1删除，0未删除），逻辑删除字段',
    constraint sys_user_uk
        unique (username) comment '账号保证唯一'
)
    comment '用户信息表';
```

## 角色信息表

```mysql
create table sys_role
(
    id          bigint                       not null comment '主键id'
        primary key,
    name        varchar(20)                  not null comment '角色名称',
    code        varchar(20)                  not null comment '角色代码',
    create_id   varchar(20)                  null comment '创建人',
    create_time datetime                     not null comment '创建时间',
    update_id   varchar(20)                  null comment '更新人',
    update_time datetime                     null comment '更新时间',
    is_deleted  tinyint unsigned default '0' not null comment '是否删除（1删除，0未删除），逻辑删除字段',
    constraint sys_role_uk_code
        unique (code)
)
    comment '角色信息表';
```

## 用户与角色关联表

```mysql
create table sys_user_role
(
    id      bigint not null comment '主键id'
        primary key,
    user_id bigint not null comment '用户表id',
    role_id bigint not null comment '角色表id',
    constraint sys_user_role_uk
        unique (user_id, role_id)
)
    comment '用户权限关联表';
```

## 权限信息表

```mysql
create table sys_authorities
(
    id         bigint                       not null comment '主键id'
        primary key,
    name       varchar(50)                  not null comment '权限名称',
    authority  varchar(50)                  not null comment '权限编码',
    is_deleted tinyint unsigned default '0' not null comment '是否删除（1删除，0未删除），逻辑删除字段',
    constraint sys_authorities_uk_authority
        unique (authority)
)
    comment '权限信息表';
```

## 角色与权限关联表

```mysql
create table sys_role_authorities
(
    id             bigint not null comment '主键id'
        primary key,
    role_id        bigint not null comment '角色id',
    authorities_id bigint not null comment '权限id',
    constraint sys_role_authorities_uk
        unique (role_id, authorities_id)
)
    comment '角色和权限关联表';
```


## oauth2

> ClientRegistration

客户端注册信息

```mysql
create table client_registration
(
    id                           bigint                                                                 not null comment '主键'
        primary key,
    registration_id              varchar(24)                                                            not null comment '注册端标识，唯一（一个项目只能有一个项目的id）',
    client_id                    varchar(128)                                                           not null comment '客户端id',
    client_secret                varchar(128)                                                           not null comment '客户端密码',
    client_authentication_method varchar(32)                                                            not null comment '客户端认证方法',
    authorization_grant_type     varchar(24)                                                            not null comment '认证授权类型',
    redirect_uri                 varchar(255) default '{baseUrl}/{action}/oauth2/code/{registrationId}' not null comment '重定向uri（重定向到服务地址(个人项目的地址)接口）',
    scope                        varchar(255)                                                           not null comment '授权范围',
    provider_details_id          bigint                                                                 not null comment '提供商详情信息id（provider_details关联）',
    client_name                  varchar(24)                                                            null comment '客户端名字',
    redirect                     varchar(255)                                                           not null comment '前后端分离 - 重定向的地址'
)
    comment '客户端注册信息';
```

> ProviderDetails

客户端提供的信息。例如：登录接口地址，用户信息接口等

```mysql
create table provider_details
(
    id                     bigint        not null comment '主键'
        primary key,
    registration_id        varchar(24)   not null comment '第三方服务商标识',
    authorization_uri      varchar(255)  not null comment '第三方服务商(github)的登录接口',
    token_uri              varchar(255)  not null comment '第三方服务商(github)获取token的接口',
    user_info_endpoint_id  bigint        not null comment '用户信息端点id',
    jwk_set_uri            varchar(255)  null,
    issuer_uri             varchar(255)  null,
    configuration_metadata varchar(2000) null comment '配置源数据',
    constraint registration_id_un
        unique (registration_id)
)
    comment 'oauth2 服务商提供的信息';
```

> UserInfoEndpoint
```mysql
create table user_info_endpoint
(
    id                       bigint       not null comment '主键',
    uri                      varchar(255) not null comment '获取第三方服务商用户信息接口',
    authentication_method    varchar(32)  not null comment '认证方法。可选值：header，form，query',
    user_name_attribute_name varchar(32)  not null comment '第三方用户名的字段'
)
    comment '用户信息端点';
```