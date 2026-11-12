package com.umc.movie.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // AUTH
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH401_1", "이메일 또는 비밀번호가 올바르지 않습니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH401_2", "로그인이 필요한 요청입니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH401_3", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH401_4", "토큰이 만료되었습니다. 다시 로그인해주세요."),

    // MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "해당 회원을 찾을 수 없습니다."),
    NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "MEMBER409_1", "이미 사용 중인 닉네임입니다."),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "MEMBER409_2", "이미 사용 중인 이메일입니다."),

    // RATING
    RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "RATING404_1", "해당 평점을 찾을 수 없습니다."),
    RATING_FORBIDDEN(HttpStatus.FORBIDDEN, "RATING403_1", "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다."),
    RATING_DUPLICATED(HttpStatus.CONFLICT, "RATING409_1", "이미 이 영화에 평점을 남기셨습니다.");

    private final HttpStatus status;  // 응답으로 내보낼 HTTP 상태 코드
    private final String code;        // response body의 code 필드
    private final String message;     // response body의 message 필드
}
