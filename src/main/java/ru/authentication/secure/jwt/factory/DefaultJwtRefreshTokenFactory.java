package ru.authentication.secure.jwt.factory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.authentication.domain.entity.jwt.JwtToken;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.function.Function;

import static ru.authentication.secure._static.SecureStatic.PREFIX_FOR_AUTHORITIES;


public class DefaultJwtRefreshTokenFactory implements Function<Authentication, JwtToken> {

    public static Duration REFRESH_TOKEN_Ttl = Duration.ofDays(30);



    @Override
    public JwtToken apply(Authentication authentication) {

        var authorities = new LinkedList<String>();
        authorities.add("JWT_REFRESH");
        authorities.add("JWT_LOGOUT");
        authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> PREFIX_FOR_AUTHORITIES + authority)
                .forEach(authorities::add);
        var now = Instant.now();
        return new JwtToken(authentication.getName(), authorities, now, now.plus(REFRESH_TOKEN_Ttl));
    }

}
