package com.example.springboot_mission.movie.code;

import com.example.springboot_mission.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FavoriteErrorCode implements BaseErrorCode {

    ALREADY_FAVORITE(
            HttpStatus.CONFLICT,
            "FAVORITE4091",
            "이미 이 영화를 즐겨찾기 하셨습니다."
    ),
    FAVORITE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "FAVORITE4041",
            "즐겨찾기한 영화를 찾을 수 없습니다."
    ),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

