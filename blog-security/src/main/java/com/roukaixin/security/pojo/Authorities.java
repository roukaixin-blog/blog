package com.roukaixin.security.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限信息
 * @author pankx
 * @TableName sys_authorities
 */
@TableName(value ="sys_authorities")
@Data
public class Authorities {

    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限编码
     */
    private String authority;

    /**
     * 是否删除（1删除，0未删除），逻辑删除字段
     */
    private Integer isDeleted;

}