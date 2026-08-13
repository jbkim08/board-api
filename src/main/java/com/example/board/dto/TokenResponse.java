package com.example.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType; // "Bearer"
    private long expiresIn;   // 초 단위 만료시간
    private String username;
    private String nickname;

    public static TokenResponse of(String accessToken, String refreshToken, long expiresInMs, String username, String nickname) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresInMs / 1000)
                .username(username)
                .nickname(nickname)
                .build();
    }
}

