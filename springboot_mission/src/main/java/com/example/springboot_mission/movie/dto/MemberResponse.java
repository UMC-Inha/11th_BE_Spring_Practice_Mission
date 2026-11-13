package com.example.springboot_mission.movie.dto;

public record MemberResponse(
        Long memberId,
        String email,
        String nickname
) {
}
