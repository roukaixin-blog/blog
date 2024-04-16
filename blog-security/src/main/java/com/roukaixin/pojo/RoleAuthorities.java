package com.roukaixin.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色和权限关联表
 * @author pankx
 * @TableName sys_role_authorities
 */
@TableName(value ="sys_role_authorities")
@Data
public class RoleAuthorities {
    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 权限id
     */
    private Long authoritiesId;

}