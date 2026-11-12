package com.umc.movie.member.repository;

import com.umc.movie.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    // 나 자신을 빼고 이 넥네임을 쓰는 회원이 있는가?
    boolean existsByNicknameAndMemberIdNot(String nickname, Long memberId);
}