package com.umc.movie.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException{

    private final HttpStatus status; // 응답으로 내보낼 Http status code
    private final String code; //response body의 code필드

    public ApiException(HttpStatus status, String code, String message){
        super(message); // message는 부모 클래스인 RuntimeException이 들고 있음
        this.status = status;
        this.code = code;
    }

    public ApiException(ErrorCode errorCode){
        this(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
    }
}
