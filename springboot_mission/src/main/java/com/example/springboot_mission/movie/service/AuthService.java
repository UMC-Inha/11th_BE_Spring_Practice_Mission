package com.example.springboot_mission.movie.service;

import com.example.springboot_mission.global.exception.ProjectException;
import com.example.springboot_mission.global.util.JwtUtil;
import com.example.springboot_mission.movie.code.AuthErrorCode;
import com.example.springboot_mission.movie.dto.LoginRequest;
import com.example.springboot_mission.movie.dto.LoginResponse;
import com.example.springboot_mission.movie.entity.Member;
import com.example.springboot_mission.movie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // 6주차 회원가입에서 등록해둔 Bean입니다
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        // 1. 이메일로 회원을 찾습니다.
        //    없어도 "가입되지 않은 이메일"이라고 알려주지 않고 아래와 같은 예외를 던집니다.
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new ProjectException(AuthErrorCode.INVALID_LOGIN_INFORMATION));

        // 2. 입력한 비밀번호를 저장된 해시와 비교합니다.
        //    matches()가 저장된 해시에서 salt를 꺼내 같은 방식으로 해시한 뒤 대조해줍니다.
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ProjectException(AuthErrorCode.INVALID_LOGIN_INFORMATION);
        }

        // 3. 검증을 통과했으니 토큰을 발급합니다.
        String accessToken = jwtUtil.createAccessToken(member.getMemberId(), member.getEmail());

        return new LoginResponse(member.getNickname(), accessToken);
    }
}
