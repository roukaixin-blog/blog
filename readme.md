> 数据库脚本

创建 blog 数据库

```mysql
create schema blog collate utf8mb4_general_ci;
```

创建用户表

````mysql
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
    comment '用户表';
````

## oauth2

> ClientRegistration

客户端注册信息

````mysql
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
````

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
````mysql
create table user_info_endpoint
(
    id                       bigint       not null comment '主键',
    uri                      varchar(255) not null comment '获取第三方服务商用户信息接口',
    authentication_method    varchar(32)  not null comment '认证方法。可选值：header，form，query',
    user_name_attribute_name varchar(32)  not null comment '第三方用户名的字段'
)
    comment '用户信息端点';
````

> 登陆请求地址

```shell
http://127.0.0.1:10000/authentication/oauth2/authorization/github
```
