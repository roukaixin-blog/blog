package com.roukaixin.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户权限关联表
 * @author pankx
 * @TableName sys_user_role
 */
@TableName(value ="sys_user_role")
@Data
public class UserRole {

    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 用户表id
     */
    private Long userId;

    /**
     * 角色表id
     */
    private Long roleId;

}