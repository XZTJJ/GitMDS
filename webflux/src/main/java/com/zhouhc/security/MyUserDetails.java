package com.zhouhc.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//保存 spring security 的用户信息的
@Data
public class MyUserDetails implements UserDetails {
    public Long id;

    // 账号
    public String username;

    // 密码
    public String password;

    // 创建用户id
    private Integer createUser;

    // 创建时间
    private Date createTime;

    // 更新用户id
    private Integer updateUser;

    // 更新时间
    private Date updateTime;

    // 是否启用，（0：禁用，1：启用）
    private Boolean enabled;

    private String email;

    private byte[] clientPfx;

    private Integer certDays;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.id != null && this.username != null && this.id == 1 && this.username.equals("admin"))
            return Stream.of("ROLE_ADMIN").map(value -> new SimpleGrantedAuthority(value)).collect(Collectors.toList());
        return Stream.of("ROLE_NOTADMIN").map(value -> new SimpleGrantedAuthority(value)).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
