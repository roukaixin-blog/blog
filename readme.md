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