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
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
