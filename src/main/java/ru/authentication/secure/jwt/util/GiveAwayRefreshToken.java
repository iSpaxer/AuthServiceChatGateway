package ru.authentication.secure.jwt.util;


import ru.authentication.domain.entity.jwt.JwtToken;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

public class GiveAwayRefreshToken implements Function<JwtToken, Boolean> {

    private int standard_for_issuing_percent = 50;

    @Override
    public Boolean apply(JwtToken jwtToken) {
        // дефолтное время
        var defaultBetween = Duration.between(jwtToken.expiresAt(), jwtToken.createdAt());

        // время когда мы можем выдать новый refreshTOken
        var timeOfIssue = (standard_for_issuing_percent / 100) * defaultBetween.toMillis();

        // время которое прошло
        var timeLeft = Duration.between(jwtToken.expiresAt(), Instant.now());

        return timeLeft.toMillis() > timeOfIssue;
    }
}
