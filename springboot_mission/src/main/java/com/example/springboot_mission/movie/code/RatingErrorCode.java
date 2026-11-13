package com.example.springboot_mission.movie.code;

import com.example.springboot_mission.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RatingErrorCode implements BaseErrorCode {

    RATING_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RATING4041",
            "해당 평점을 찾을 수 없습니다."
    ),
    NOT_RATING_OWNER(
            HttpStatus.FORBIDDEN,
            "RATING4031",
            "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다."
    ),
    RATING_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "RATING4091",
            "이미 이 영화에 평점을 남기셨습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
