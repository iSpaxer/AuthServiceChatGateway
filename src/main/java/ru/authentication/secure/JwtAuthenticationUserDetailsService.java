package ru.authentication.secure;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.authentication.domain.entity.jwt.JwtToken;

import java.time.Instant;

public class JwtAuthenticationUserDetailsService
        implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken) throws UsernameNotFoundException {
        if (authenticationToken.getPrincipal() instanceof JwtToken token) {
            return new TokenAdmin(token.username(), "nopassword", true, true,
                            token.expiresAt().isAfter(Instant.now()),
                    true,
                    token.authorities().stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList(), token);
        }
        throw new UsernameNotFoundException("Principal must me of type Token");
    }

}
