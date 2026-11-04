package com.example.springboot_mission.movie.service;

import com.example.springboot_mission.global.exception.ProjectException;
import com.example.springboot_mission.movie.code.AuthErrorCode;
import com.example.springboot_mission.movie.dto.EmailCheckResponse;
import com.example.springboot_mission.movie.dto.MemberResponse;
import com.example.springboot_mission.movie.dto.NicknameCheckResponse;
import com.example.springboot_mission.movie.dto.SignUpRequest;
import com.example.springboot_mission.movie.entity.Member;
import com.example.springboot_mission.movie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public NicknameCheckResponse checkNickname(String nickname) {
        boolean exists = memberRepository.existsByNickname(nickname);
        return new NicknameCheckResponse(!exists);
    }

    @Transactional(readOnly = true)
    public EmailCheckResponse checkEmail(String email) {
        boolean exists = memberRepository.existsByEmail(email);
        return new EmailCheckResponse(!exists);
    }

    @Transactional
    public MemberResponse signUp(SignUpRequest request) {
        if(memberRepository.existsByEmail(request.email())) {
            throw new ProjectException(AuthErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        if(memberRepository.existsByNickname(request.nickname())) {
            throw new ProjectException(AuthErrorCode.NICKNAME_ALREADY_REGISTERED);
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        Member savedMember = memberRepository.save(member);

        return new MemberResponse(
                savedMember.getMemberId(),
                savedMember.getEmail(),
                savedMember.getNickname()
                );
    }
}
