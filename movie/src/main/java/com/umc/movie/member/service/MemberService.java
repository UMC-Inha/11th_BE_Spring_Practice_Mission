package com.umc.movie.member.service;

import com.umc.movie.auth.dto.request.LoginRequest;
import com.umc.movie.auth.dto.response.LoginResponse;
import com.umc.movie.common.exception.ApiException;
import com.umc.movie.common.exception.ErrorCode;
import com.umc.movie.common.util.JwtUtil;
import com.umc.movie.member.dto.request.MemberUpdateRequest;
import com.umc.movie.member.dto.request.SignUpRequest;
import com.umc.movie.member.dto.response.EmailCheckResponse;
import com.umc.movie.member.dto.response.MemberInfoResponse;
import com.umc.movie.member.dto.response.MemberResponse;
import com.umc.movie.member.dto.response.NicknameCheckResponse;
import com.umc.movie.member.entity.Member;
import com.umc.movie.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
        if(memberRepository.existsByEmail(request.email())){
            throw new ApiException(ErrorCode.EMAIL_DUPLICATED);
        }
        if(memberRepository.existsByNickname(request.nickname())){
            throw new ApiException(ErrorCode.NICKNAME_DUPLICATED);
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
    public LoginResponse login(LoginRequest request){
        //1. 이메일로 회원 찾음
        //없다고 "가입되지 않은 이메일" X, "이메일 또는 비밀번호가 올바르지 않습니다" O
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(ErrorCode.LOGIN_FAILED));

        //2. 입력한 비밀번호를 저장된 해시와 비교
        if(!passwordEncoder.matches(request.password(), member.getPassword())){
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }

        //3. 로그인 상태를 유지할 수 있도록 토큰 발급
        String accessToken = jwtUtil.createAccessToken(member.getMemberId(), member.getEmail());

        return new LoginResponse(member.getNickname(), accessToken);
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberInfoResponse.from(member);
    }

    public MemberInfoResponse updateMyInfo(Long memberId, MemberUpdateRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        //나를 제외한 다른 회원이 해당 닉네임을 쓰고 있다면 막는다.
        //DB에도 UNIQUE 제약이 걸려 있지만 제약 위반 예외 대신 우리가 정한 응답을 주기 위해서 먼저 확인
        if(memberRepository.existsByNicknameAndMemberIdNot(request.nickname(), memberId)){
            throw new ApiException(ErrorCode.NICKNAME_DUPLICATED);
        }

        member.updateNickname(request.nickname());
        memberRepository.save(member);

        return MemberInfoResponse.from(member);
    }
}
