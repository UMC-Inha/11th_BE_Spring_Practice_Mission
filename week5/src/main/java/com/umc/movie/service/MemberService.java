package com.umc.movie.service;

import com.umc.movie.dto.NicknameCheckResponse;
import com.umc.movie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public NicknameCheckResponse checkNickname(String nickname) {
        boolean available = !memberRepository.existsByNickname(nickname);
        return new NicknameCheckResponse(available);
    }
}