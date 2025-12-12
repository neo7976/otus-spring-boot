package ru.dsobin.otus.spring.boot.config;

import com.auth0.jwt.interfaces.Claim;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.dsobin.otus.spring.boot.config.jwt.UserDetailsImpl;
import ru.dsobin.otus.spring.boot.config.jwt.UserJwt;


import java.util.Map;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    //Требуется сделать запрос, в теории, если токен истек и требуется его обновить в user-service. Сейчас просто заглушка на будущее
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        UserJwt user = new UserJwt();
        user.setId(1L);
        user.setUsername("admin");
        return UserDetailsImpl.build(user);
    }

    public UserDetailsImpl loadUserByClaims(Map<String, Claim> claims) throws UsernameNotFoundException {
        return UserDetailsImpl.build(UserJwt.create(claims));
    }
}
