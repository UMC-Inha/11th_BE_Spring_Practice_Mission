package com.umc.movie.controller;

import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.req.CreateMovieRecordRequest;
import com.umc.movie.dto.res.MovieRecordResponse;
import com.umc.movie.interceptor.AuthInterceptor;
import com.umc.movie.service.MovieRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "영화 기록 API", description = "영화 기록 관련 기능")
@RestController
@RequiredArgsConstructor
public class MovieRecordController {

    private final MovieRecordService movieRecordService;

    @PostMapping("/movie-records")
    public ResponseEntity<ApiResponse<MovieRecordResponse>> create(
                @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
                @RequestBody @Valid CreateMovieRecordRequest request
    ){
        MovieRecordResponse response = movieRecordService.create(memberId, request);
        return ResponseEntity.status(201).body(ApiResponse.created("영화 기록을 저장하였습니다.", response));
    }

}
