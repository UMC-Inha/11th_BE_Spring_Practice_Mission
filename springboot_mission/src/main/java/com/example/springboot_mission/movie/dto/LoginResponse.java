package com.example.springboot_mission.movie.dto;

public record LoginResponse(
        String nickname,
        String accessToken
) {
}