package com.example.springboot_mission.movie.service;

import com.example.springboot_mission.global.exception.ProjectException;
import com.example.springboot_mission.movie.code.AuthErrorCode;
import com.example.springboot_mission.movie.code.MemberErrorCode;
import com.example.springboot_mission.movie.dto.*;
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

    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberInfoResponse updateMyInfo(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.MEMBER_NOT_FOUND));

        if(memberRepository.existsByNicknameAndMemberIdNot(request.nickname(), memberId)) {
            throw new ProjectException(MemberErrorCode.NICKNAME_ALREADY_USED);
        }

        member.updateNickName(request.nickname());
        memberRepository.save(member);

        return MemberInfoResponse.from(member);
    }
}
