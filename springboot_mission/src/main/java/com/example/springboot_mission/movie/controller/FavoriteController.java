package com.example.springboot_mission.movie.controller;

import com.example.springboot_mission.global.response.ApiResponse;
import com.example.springboot_mission.movie.config.SwaggerConfig;
import com.example.springboot_mission.movie.dto.CreateFavoriteRequest;
import com.example.springboot_mission.movie.dto.FavoriteResponse;
import com.example.springboot_mission.movie.dto.FavoriteStatusResponse;
import com.example.springboot_mission.movie.interceptor.AuthInterceptor;
import com.example.springboot_mission.movie.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "즐겨찾기", description = "로그인한 회원의 영화 즐겨찾기 API")
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH) // 이 컨트롤러의 모든 API가 토큰을 요구합니다
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "즐겨찾기 등록",
            description = "Bearer 토큰이 필요한 API입니다. 토큰의 회원이 TMDB 영화 ID 하나를 즐겨찾기에 추가합니다. memberId는 요청으로 받지 않습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "즐겨찾기 등록 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON201",
                                      "message": "즐겨찾기를 등록했습니다.",
                                      "result": {
                                        "favoriteId": 1,
                                        "movieId": 550,
                                        "createdAt": "2026-09-13T21:00:00"
                                      }
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
                                      "message": "영화 ID는 양수여야 합니다.",
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
                    responseCode = "409", description = "이미 즐겨찾기한 영화 또는 DB 제약 위반",
                    content = @Content(mediaType = "application/json", examples = {
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
    @PostMapping("/favorites")
    public ResponseEntity<ApiResponse<FavoriteResponse>> create(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid CreateFavoriteRequest request
    ) {
        FavoriteResponse response = favoriteService.create(memberId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("즐겨찾기를 등록했습니다.", response));
    }

    @Operation(
            summary = "내 즐겨찾기 조회",
            description = "Bearer 토큰이 필요한 API입니다. 토큰의 회원이 등록한 즐겨찾기를 최신 등록 순으로 조회합니다. 등록한 즐겨찾기가 없으면 result는 빈 배열입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "내 즐겨찾기 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "내 즐겨찾기 목록을 조회했습니다.",
                                      "result": [
                                        {
                                          "favoriteId": 1,
                                          "movieId": 550,
                                          "createdAt": "2026-09-13T21:00:00"
                                        }
                                      ]
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
                    }))
    })
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getMyFavorites(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        List<FavoriteResponse> response = favoriteService.getMyFavorites(memberId);
        return ResponseEntity.ok(ApiResponse.ok("내 즐겨찾기 목록을 조회했습니다.", response));
    }

    @Operation(
            summary = "즐겨찾기 여부 조회",
            description = "Bearer 토큰이 필요한 API입니다. 토큰의 회원이 해당 TMDB 영화를 즐겨찾기했는지 확인합니다. 즐겨찾기하지 않은 영화도 오류가 아니라 200과 isFavorite: false를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "즐겨찾기 여부 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "즐겨찾기 여부를 조회했습니다.",
                                      "result": {
                                        "isFavorite": true
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
                    }))
    })
    @GetMapping("/favorites/{movieId}")
    public ResponseEntity<ApiResponse<FavoriteStatusResponse>> getStatus(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long movieId
    ) {
        FavoriteStatusResponse response = favoriteService.getStatus(memberId, movieId);
        return ResponseEntity.ok(ApiResponse.ok("즐겨찾기 여부를 조회했습니다.", response));
    }

    @Operation(
            summary = "즐겨찾기 해제",
            description = "Bearer 토큰이 필요한 API입니다. 토큰의 회원이 등록한 영화의 즐겨찾기를 해제합니다. movieId는 TMDB 영화 ID이며 favoriteId가 아닙니다. 성공 시 200과 result: null을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "즐겨찾기 해제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "즐겨찾기를 해제했습니다.",
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
                    responseCode = "404", description = "해제할 즐겨찾기를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "FAVORITE4041",
                                      "message": "즐겨찾기한 영화를 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """)
                    }))
    })
    @DeleteMapping("/favorites/{movieId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long movieId
    ) {
        favoriteService.delete(memberId, movieId);
        return ResponseEntity.ok(ApiResponse.ok("즐겨찾기를 해제했습니다.", null));
    }
}
