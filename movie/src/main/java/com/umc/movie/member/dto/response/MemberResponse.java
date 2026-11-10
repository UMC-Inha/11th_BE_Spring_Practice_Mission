package com.umc.movie.member.dto.response;

public record MemberResponse(
        Long memberId,
        String email,
        String nickname
) {
}
