package ru.authentication.secure.jwt.factory;



import ru.authentication.domain.entity.jwt.JwtToken;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.function.Function;

import static ru.authentication.secure._static.SecureStatic.PREFIX_FOR_AUTHORITIES;


public class DefaultJwtAccessTokenFactory implements Function<JwtToken, JwtToken> {

    Duration tokenTtl = Duration.ofMinutes(5);

    @Override
    public JwtToken apply(JwtToken token) {
        var authorities = new LinkedList<String>();
        token.authorities()
                .stream()
                .filter(authority -> authority.startsWith(PREFIX_FOR_AUTHORITIES))
                .map(authority -> authority.substring(PREFIX_FOR_AUTHORITIES.length()))
                .forEach(authorities::add);
        var now = Instant.now();
        return new JwtToken(token.username(), authorities, now, now.plus(tokenTtl));
    }

}
