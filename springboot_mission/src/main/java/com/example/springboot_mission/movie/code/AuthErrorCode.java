package com.example.springboot_mission.movie.code;

import com.example.springboot_mission.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    EMAIL_ALREADY_REGISTERED(
            HttpStatus.CONFLICT,
            "AUTH4091",
            "이미 가입된 이메일입니다."
    ),
    NICKNAME_ALREADY_REGISTERED(
            HttpStatus.CONFLICT,
            "AUTH4092",
            "이미 존재하는 닉네임입니다."
    ),
    TOKEN_EXPIRED(
            HttpStatus.UNAUTHORIZED,
            "AUTH4014",
            "토큰이 만료되었습니다."
    ),
    INVALID_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH4013",
            "유효하지 않은 토큰입니다."
    ),
    INVALID_LOGIN_INFORMATION(
            HttpStatus.UNAUTHORIZED,
            "AUTH4011",
            "이메일 또는 비밀번호가 올바르지 않습니다."
    ),
    REQUEST_NEED_LOGIN(
            HttpStatus.UNAUTHORIZED,
            "AUTH4012",
            "로그인이 필요한 요청입니다."
    ),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
