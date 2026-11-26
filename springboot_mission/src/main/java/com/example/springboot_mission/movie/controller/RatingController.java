package com.example.springboot_mission.movie.controller;

import com.example.springboot_mission.global.response.ApiResponse;
import com.example.springboot_mission.movie.config.SwaggerConfig;
import com.example.springboot_mission.movie.dto.CreateRatingRequest;
import com.example.springboot_mission.movie.dto.RatingResponse;
import com.example.springboot_mission.movie.dto.UpdateRatingRequest;
import com.example.springboot_mission.movie.interceptor.AuthInterceptor;
import com.example.springboot_mission.movie.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "평점", description = "영화 평점 등록·수정·삭제와 유저별 평점 조회 API")
public class RatingController {

    private final RatingService ratingService;

    @Operation(
            summary = "유저별 평점 조회",
            description = "토큰 없이 호출할 수 있는 공개 API입니다. 지정한 회원의 평점을 최신 등록 순으로 조회합니다. page는 0부터 시작하고 size의 기본값은 10입니다. 공통 응답으로 감싸지 않은 배열을 반환하며, 결과가 없으면 빈 배열을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "유저별 평점 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                      {
                                        "ratingId": 1,
                                        "movieId": 550,
                                        "score": 5,
                                        "comment": "다시 보고 싶은 영화입니다.",
                                        "createdAt": "2026-09-13T21:00:00",
                                        "updatedAt": "2026-09-13T21:00:00"
                                      }
                                    ]
                                    """)
                    })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "조회할 회원을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "MEMBER4041",
                                      "message": "해당 회원을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """)
                    }))
    })
    @GetMapping("/members/{memberId}/ratings")
    public List<RatingResponse> getMemberRatings(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        return ratingService.getRatingsByMember(memberId, page, size);
    }

    @Operation(
            summary = "평점 등록",
            description = "Bearer 토큰이 필요한 API입니다. 토큰의 회원이 TMDB 영화에 평점을 남깁니다. 같은 회원은 같은 영화에 한 번만 등록할 수 있으며 memberId는 요청으로 받지 않습니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "평점 등록 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON201",
                                      "message": "평점 등록에 성공했습니다.",
                                      "result": {
                                        "ratingId": 1,
                                        "movieId": 550,
                                        "score": 5,
                                        "comment": "다시 보고 싶은 영화입니다.",
                                        "createdAt": "2026-09-13T21:00:00",
                                        "updatedAt": "2026-09-13T21:00:00"
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
                    responseCode = "409", description = "이미 평점을 남긴 영화 또는 DB 제약 위반",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "평점 중복", value = """
                                    {
                                      "isSuccess": false,
                                      "code": "RATING4091",
                                      "message": "이미 이 영화에 평점을 남기셨습니다.",
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
    @PostMapping("/ratings")
    public ResponseEntity<ApiResponse<RatingResponse>> create(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody CreateRatingRequest request
    ) {
        RatingResponse response = ratingService.create(memberId, request);
        return ResponseEntity.status(201).body(ApiResponse.created("평점 등록에 성공했습니다.", response));
    }

    @Operation(
            summary = "평점 수정",
            description = "Bearer 토큰이 필요한 API입니다. ratingId로 지정한 평점의 별점과 후기를 수정합니다. 토큰의 회원이 작성한 평점만 수정할 수 있습니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "평점 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "평점 수정에 성공했습니다.",
                                      "result": {
                                        "ratingId": 1,
                                        "movieId": 550,
                                        "score": 5,
                                        "comment": "다시 보고 싶은 영화입니다.",
                                        "createdAt": "2026-09-13T21:00:00",
                                        "updatedAt": "2026-09-13T21:00:00"
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
                                      "message": "별점은 5점 이하여야 합니다.",
                                      "result": null
                                    }
                                    """)
                    })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "다른 회원이 작성한 평점",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "RATING4031",
                                      "message": "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.",
                                      "result": null
                                    }
                                    """)
                    })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "평점을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "RATING4041",
                                      "message": "해당 평점을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """)
                    }))
    })
    @PatchMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<RatingResponse>> update(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long ratingId,
            @Valid @RequestBody UpdateRatingRequest request
    ) {
        RatingResponse response = ratingService.update(memberId, ratingId, request);
        return ResponseEntity.ok(ApiResponse.ok("평점 수정에 성공했습니다.", response));
    }

    @Operation(
            summary = "평점 삭제",
            description = "Bearer 토큰이 필요한 API입니다. 토큰의 회원이 작성한 평점만 삭제할 수 있습니다. 삭제 성공 시 200과 공통 응답을 반환하며 result는 null입니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "평점 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "평점 삭제에 성공했습니다.",
                                      "result": null
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
                    responseCode = "403", description = "다른 회원이 작성한 평점",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "RATING4031",
                                      "message": "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.",
                                      "result": null
                                    }
                                    """)
                    })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "평점을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "RATING4041",
                                      "message": "해당 평점을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """)
                    }))
    })
    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long ratingId
    ) {
        ratingService.delete(memberId, ratingId);
        return ResponseEntity.ok(ApiResponse.ok("평점 삭제에 성공했습니다.", null));
    }
}
