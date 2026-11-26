package com.example.springboot_mission.movie.controller;

import com.example.springboot_mission.global.response.ApiResponse;
import com.example.springboot_mission.movie.config.SwaggerConfig;
import com.example.springboot_mission.movie.dto.CreateMovieRecordRequest;
import com.example.springboot_mission.movie.dto.MovieRecordResponse;
import com.example.springboot_mission.movie.interceptor.AuthInterceptor;
import com.example.springboot_mission.movie.service.MovieRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "영화 기록", description = "평점과 즐겨찾기를 함께 저장하는 API")
public class MovieRecordController {

    private final MovieRecordService movieRecordService;

    @Operation(
            summary = "영화 기록 저장",
            description = "Bearer 토큰이 필요한 API입니다. 평점을 저장하고 addToFavorites가 true이면 즐겨찾기도 같은 트랜잭션에서 저장합니다. false이면 평점만 저장하고 응답의 favorite는 null입니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "영화 기록 저장 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON201",
                                      "message": "영화 기록을 저장했습니다.",
                                      "result": {
                                        "rating": {
                                          "ratingId": 1,
                                          "movieId": 550,
                                          "score": 5,
                                          "comment": "다시 보고 싶은 영화입니다.",
                                          "createdAt": "2026-09-13T21:00:00",
                                          "updatedAt": "2026-09-13T21:00:00"
                                        },
                                        "favorite": {
                                          "favoriteId": 1,
                                          "movieId": 550,
                                          "createdAt": "2026-09-13T21:00:00"
                                        }
                                      }
                                    }
                                    """)
                    })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "토큰이 없거나 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "토큰 누락", value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH4012",
                                      "message": "로그인이 필요한 요청입니다.",
                                      "result": null
                                    }
                                    """),
                            @ExampleObject(name = "유효하지 않은 토큰", value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH4013",
                                      "message": "유효하지 않은 토큰입니다.",
                                      "result": null
                                    }
                                    """),
                            @ExampleObject(name = "만료된 토큰", value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH4014",
                                      "message": "토큰이 만료되었습니다.",
                                      "result": null
                                    }
                                    """)
                    })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "요청 값 검증 실패 (message는 실패한 검증 조건에 따라 달라짐)",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON400",
                                      "message": "별점은 1점 이상이어야 합니다.",
                                      "result": null
                                    }
                                    """)
                    })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "토큰의 회원을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "MEMBER4041",
                                      "message": "해당 회원을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """)
                    })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "평점·즐겨찾기 중복 또는 DB 제약 위반",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "평점 중복", value = """
                                    {
                                      "isSuccess": false,
                                      "code": "RATING4091",
                                      "message": "이미 이 영화에 평점을 남기셨습니다.",
                                      "result": null
                                    }
                                    """),
                            @ExampleObject(name = "즐겨찾기 중복", value = """
                                    {
                                      "isSuccess": false,
                                      "code": "FAVORITE4091",
                                      "message": "이미 이 영화를 즐겨찾기 하셨습니다.",
                                      "result": null
                                    }
                                    """),
                            @ExampleObject(name = "DB 제약 위반", value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON409",
                                      "message": "요청이 현재 리소스 상태와 충돌합니다.",
                                      "result": null
                                    }
                                    """)
                    }))
    })
    @PostMapping("/movie-records")
    public ResponseEntity<ApiResponse<MovieRecordResponse>> create(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid CreateMovieRecordRequest request
    ) {
        MovieRecordResponse response = movieRecordService.create(memberId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("영화 기록을 저장했습니다.", response));
    }
}
