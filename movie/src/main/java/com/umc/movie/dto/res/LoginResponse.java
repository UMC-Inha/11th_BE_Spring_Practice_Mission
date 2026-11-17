package com.umc.movie.dto.res;

public record LoginResponse(
        String nickname,
        String accessToken
) {
}