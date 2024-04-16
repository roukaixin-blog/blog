package com.roukaixin.authorization.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.authorization.authority.SimpleGrantedAuthority;
import com.roukaixin.mapper.AuthoritiesMapper;
import com.roukaixin.mapper.RoleAuthoritiesMapper;
import com.roukaixin.mapper.UserMapper;
import com.roukaixin.mapper.UserRoleMapper;
import com.roukaixin.pojo.Authorities;
import com.roukaixin.pojo.RoleAuthorities;
import com.roukaixin.pojo.User;
import com.roukaixin.pojo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户名/密码登陆获取账号信息
 *
 * @author 不北咪
 * @date 2024/1/19 上午10:42
 */
@Service
public class UsernamePasswordUserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    private final UserRoleMapper userRoleMapper;

    private final RoleAuthoritiesMapper roleAuthoritiesMapper;

    private final AuthoritiesMapper authoritiesMapper;

    @Autowired
    public UsernamePasswordUserDetailsServiceImpl(UserMapper userMapper,
                                                  UserRoleMapper userRoleMapper,
                                                  RoleAuthoritiesMapper roleAuthoritiesMapper,
                                                  AuthoritiesMapper authoritiesMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleAuthoritiesMapper = roleAuthoritiesMapper;
        this.authoritiesMapper = authoritiesMapper;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在，请注册！");
        }
        List<UserRole> userRoles = userRoleMapper
                .selectList(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, user.getId()));
        if (!userRoles.isEmpty()) {
            List<RoleAuthorities> roleAuthorities = roleAuthoritiesMapper.selectList(
                    Wrappers.<RoleAuthorities>lambdaQuery().
                            in(RoleAuthorities::getRoleId, userRoles.stream().map(UserRole::getRoleId).toList())
            );
            if (!roleAuthorities.isEmpty()) {
                List<Authorities> authorities = authoritiesMapper
                        .selectBatchIds(roleAuthorities.stream().map(RoleAuthorities::getAuthoritiesId).toList());
                List<String> list = authorities.stream().map(Authorities::getAuthority).distinct().toList();
                Set<GrantedAuthority> simpleGrantedAuthorities = new HashSet<>();
                list.forEach(e -> simpleGrantedAuthorities.add(new SimpleGrantedAuthority(e)));
                user.setAuthorities(simpleGrantedAuthorities);
            }
        }
        return user;
    }
}
