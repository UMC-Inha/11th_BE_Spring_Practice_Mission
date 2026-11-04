package com.example.springboot_mission.global.exception;


import com.example.springboot_mission.global.response.ApiResponse;
import com.example.springboot_mission.global.response.code.BaseErrorCode;
import com.example.springboot_mission.global.response.code.GeneralErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse(GeneralErrorCode.BAD_REQUEST.getMessage());

        return ResponseEntity
                .status(GeneralErrorCode.BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.failure(GeneralErrorCode.BAD_REQUEST.getCode(), message));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException exception) {
        return failure(GeneralErrorCode.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity
                .status(GeneralErrorCode.CONFLICT.getHttpStatus())
                .body(ApiResponse.failure(GeneralErrorCode.CONFLICT.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException exception) {
        int status = exception.getStatusCode().value();
        return ResponseEntity.status(status)
                .body(ApiResponse.failure("COMMON" + status, exception.getReason()));
    }

    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<ApiResponse<Void>> handleProjectException(
            ProjectException exception
    ) {
        return failure(exception.getErrorCode());
    }

    private ResponseEntity<ApiResponse<Void>> failure(
            BaseErrorCode errorCode
    ) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }
}
