package com.example.springboot_mission.movie.controller;

import com.example.springboot_mission.global.response.ApiResponse;
import com.example.springboot_mission.movie.dto.CreateMovieRecordRequest;
import com.example.springboot_mission.movie.dto.MovieRecordResponse;
import com.example.springboot_mission.movie.interceptor.AuthInterceptor;
import com.example.springboot_mission.movie.service.MovieRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MovieRecordController {

    private final MovieRecordService movieRecordService;

    @PostMapping("/movie-records")
    public ResponseEntity<ApiResponse<MovieRecordResponse>> create(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid CreateMovieRecordRequest request
    ) {
        MovieRecordResponse response = movieRecordService.create(memberId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("영화 기록을 저장했습니다.", response));
    }
}