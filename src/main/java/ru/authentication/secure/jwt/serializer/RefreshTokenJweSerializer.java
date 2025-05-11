package ru.authentication.secure.jwt.serializer;


import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.authentication.domain.entity.jwt.JwtToken;

import java.sql.Date;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenJweSerializer implements Function<JwtToken, String> {

    JWEEncrypter jweEncrypter;
    JWEAlgorithm jweAlgorithm;
    EncryptionMethod encryptionMethod;

    public RefreshTokenJweSerializer(JWEEncrypter jweEncrypter) {
        this.jweEncrypter = jweEncrypter;
        jweAlgorithm = JWEAlgorithm.DIR;
        encryptionMethod = EncryptionMethod.A128GCM;
    }


    @Override
    public String apply(JwtToken token) {
        var jweHeader = new JWEHeader.Builder(jweAlgorithm, encryptionMethod)
                .customParam("custom", "value")
                .build();
        var jwsClaims = new JWTClaimsSet.Builder()
                .subject(token.username())
                .issueTime(Date.from(token.createdAt()))
                .expirationTime(Date.from(token.expiresAt()))
                .claim("authorities", token.authorities())
                .build();
        var encryptedJWT = new EncryptedJWT(jweHeader, jwsClaims);
        try {
            encryptedJWT.encrypt(jweEncrypter);
            return encryptedJWT.serialize();
        } catch (JOSEException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }


}
