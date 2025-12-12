package ru.dsobin.otus.spring.boot.config.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;

    /**
     * Когда получили токен
     */
    private Date issuedAt;
    /**
     * Время жизни токена
     */
    private Date expiresAt;

    private Collection<? extends GrantedAuthority> authorities;

    //потом добавить остальные поля
    public static UserDetailsImpl build(UserJwt user) {
        return UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getUsername())
                .issuedAt(user.getIssuedAt())
                .expiresAt(user.getExpiresAt())
                .authorities(user.getAuthorities())
                .build();
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
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
