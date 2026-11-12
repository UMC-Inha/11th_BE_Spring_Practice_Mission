package com.umc.movie.member.dto.response;

import com.umc.movie.member.entity.Member;
import java.time.LocalDateTime;

public record MemberInfoResponse(
        String email,
        String nickname,
        LocalDateTime createdAt
) {
    //엔티티를 그대로 응답하면 password가지 나가버리므로 DTO로 변환
    public static MemberInfoResponse from(Member member){
        return new MemberInfoResponse(
                member.getEmail(),
                member.getNickname(),
                member.getCreatedAt()
        );
    }
}
