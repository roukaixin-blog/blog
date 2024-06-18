package com.roukaixin.security.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * 用户表
 *
 * @author 不北咪
 * @date 2024/1/19 上午11:14
 * @TableName sys_user
 */
@TableName(value = "sys_user")
@ToString
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    /**
     * 主键
     */
    @Getter
    @TableId
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 账号
     */
    private String password;

    /**
     * 账号是否过期（1过期，0未过期）
     */
    private short isAccountNonExpired;

    /**
     * 账号是否被锁定（1锁定，0未锁定）
     */
    private short isAccountNonLocked;

    /**
     * 凭证是否过期（1过期，0未过期）
     */
    private short isCredentialsNonExpired;

    /**
     * 账号是否启用（1启用，0未启用）
     */
    @TableField(value = "is_enabled")
    private short enabled;

    /**
     * 是否删除（1删除，0未删除），逻辑删除字段
     */
    @Getter
    @TableLogic
    private short isDeleted;

    @TableField(exist = false)
    private Set<GrantedAuthority> authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired != 0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked != 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isAccountNonExpired != 0;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled != 0;
    }
}
