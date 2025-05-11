package ru.authentication.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.authentication.secure.JwtUserDetailsService;
import ru.authentication.secure.jwt.deserializer.AccessTokenJwsDeserializer;
import ru.authentication.secure.jwt.deserializer.RefreshTokenJweDeserializer;
import ru.authentication.secure.jwt.factory.DefaultJwtAccessTokenFactory;
import ru.authentication.secure.jwt.factory.DefaultJwtRefreshTokenFactory;
import ru.authentication.secure.jwt.serializer.AccessTokenJwsSerializer;
import ru.authentication.secure.jwt.serializer.RefreshTokenJweSerializer;


import java.text.ParseException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfiguration {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(7);
    }

    @Bean
    public JwtAuthenticationConfigurer jwtAuthenticationConfigurer(
            @Value("${jwt.access-token-key}") String accessTokenKey,
            @Value("${jwt.refresh-token-key}") String refreshTokenKey,
            JwtUserDetailsService jwtUserDetailsService,
            PasswordEncoder passwordEncoder,
            HandlerExceptionResolver handlerExceptionResolver,
            ObjectMapper objectMapper
    ) throws ParseException, JOSEException {
        return new JwtAuthenticationConfigurer(
                jwtUserDetailsService,
                passwordEncoder,
                new DefaultJwtRefreshTokenFactory(),
                new DefaultJwtAccessTokenFactory(),
                new AccessTokenJwsSerializer(new MACSigner(OctetSequenceKey.parse(accessTokenKey))),
                new RefreshTokenJweSerializer(new DirectEncrypter(OctetSequenceKey.parse(refreshTokenKey))),
                new AccessTokenJwsDeserializer(new MACVerifier(OctetSequenceKey.parse(accessTokenKey))),
                new RefreshTokenJweDeserializer(new DirectDecrypter(OctetSequenceKey.parse(refreshTokenKey))),
                handlerExceptionResolver,
                objectMapper
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationConfigurer jwtAuthenticationConfigurer
    ) throws Exception {
        http.apply(jwtAuthenticationConfigurer);

        http
                .cors().and()
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/user/**").authenticated()

                        .requestMatchers("/api/register").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().denyAll()        // Требуем аутентификацию для остальных запросов
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
