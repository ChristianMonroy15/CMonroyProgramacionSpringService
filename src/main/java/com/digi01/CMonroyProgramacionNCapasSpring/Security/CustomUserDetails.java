package com.digi01.CMonroyProgramacionNCapasSpring.Security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final int idUsuario;
    private final String username;
    private final String password;
    private final Integer isverified;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(int idUsuario, String username, String password, Integer isverified,
            Collection<? extends GrantedAuthority> authorities) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.isverified = isverified;
    }

    public Integer getIsVerified() {
        return isverified;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return isverified != null && isverified == 1;
    }
}
