package com.umc.movie.auth.dto.response;

public record LoginResponse(
        String nickname,
        String accessToken
) {
}
