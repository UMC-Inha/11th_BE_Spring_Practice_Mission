package com.umc.movie.service;

import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.MemberResponse;
import com.umc.movie.dto.NicknameCheckResponse;
import com.umc.movie.dto.SignUpRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umc.movie.dto.MemberInfoResponse;
import com.umc.movie.exception.ApiException;
import org.springframework.http.HttpStatus;
import com.umc.movie.dto.MemberUpdateRequest;
import java.sql.SQLException;

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
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        Member savedMember = memberRepository.save(member);
        return new MemberResponse(savedMember.getMemberId(), savedMember.getEmail(), savedMember.getNickname());
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MEMBER404_1",
                        "해당 회원을 찾을 수 없습니다."));

        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberInfoResponse updateMyInfo(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MEMBER404_1",
                        "해당 회원을 찾을 수 없습니다."));

        // 나를 제외한 다른 회원이 그 닉네임을 쓰고 있다면 막습니다.
        // DB에도 UNIQUE 제약이 걸려 있지만 제약 위반 예외 대신 우리가 정한 응답을 주기 위해 먼저 확인합니다.
        if (memberRepository.existsByNicknameAndMemberIdNot(request.nickname(), memberId)) {
            throw new ApiException(HttpStatus.CONFLICT, "MEMBER409_1",
                    "이미 사용 중인 닉네임입니다.");
        }

        member.updateNickname(request.nickname());

        // 검사와 저장 사이에 다른 요청이 같은 닉네임을 먼저 저장하면 DB UNIQUE 제약에 걸립니다.
        // 그때도 같은 MEMBER409_1 응답을 주기 위해 바로 DB에 반영해 보고 중복 키 오류만 바꿉니다.
        try {
            memberRepository.saveAndFlush(member);
        } catch (DataIntegrityViolationException exception) {
            if (isDuplicateKey(exception)) {
                throw new ApiException(HttpStatus.CONFLICT, "MEMBER409_1",
                        "이미 사용 중인 닉네임입니다.");
            }
            throw exception;
        }

        return MemberInfoResponse.from(member);
    }

    // MySQL 중복 키 오류 번호는 1062입니다.
    private boolean isDuplicateKey(DataIntegrityViolationException exception) {
        return exception.getMostSpecificCause() instanceof SQLException sqlException
                && sqlException.getErrorCode() == 1062;
    }

}