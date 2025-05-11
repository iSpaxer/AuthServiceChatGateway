package ru.authentication.web.dto.jwt;

public record JwtResponse(String accessToken, String expiryAccessToken,
                          String refreshToken, String expiryRefreshToken) {
}
