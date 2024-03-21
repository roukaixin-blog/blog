package com.roukaixin.pojo;

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
@TableName(value ="sys_user")
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
    @TableField(value = "is_account_non_expired")
    private boolean accountNonExpired;

    /**
     * 账号是否被锁定（1锁定，0未锁定）
     */
    @TableField(value = "is_account_non_locked")
    private boolean accountNonLocked;

    /**
     * 凭证是否过期（1过期，0未过期）
     */
    @TableField(value = "is_credentials_non_expired")
    private boolean credentialsNonExpired;

    /**
     * 账号是否启用（1启用，0未启用）
     */
    @TableField(value = "is_enabled")
    private boolean enabled;

    /**
     * 是否删除（1删除，0未删除），逻辑删除字段
     */
    @Getter
    @TableField("is_deleted")
    @TableLogic
    private boolean deleted;

    @TableField(exist = false)
    private Set<GrantedAuthority> authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
