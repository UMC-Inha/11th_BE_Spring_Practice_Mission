package com.example.springboot_mission.movie.code;

import com.example.springboot_mission.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    MEMBER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "MEMBER4041",
            "해당 회원을 찾을 수 없습니다."
    ),
    NICKNAME_ALREADY_USED(
            HttpStatus.CONFLICT,
            "MEMBER4091",
            "이미 사용중인 닉네임입니다."
    ),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

