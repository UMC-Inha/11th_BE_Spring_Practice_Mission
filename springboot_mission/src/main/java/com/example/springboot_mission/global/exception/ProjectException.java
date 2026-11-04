package com.example.springboot_mission.global.exception;

import com.example.springboot_mission.global.response.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class ProjectException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public ProjectException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
