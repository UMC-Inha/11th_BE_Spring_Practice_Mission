package com.umc.movie.dto.res;

public record MemberResponse(
        Long memberId,
        String email,
        String nickname
) {
}
