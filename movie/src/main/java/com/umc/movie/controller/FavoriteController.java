package com.umc.movie.controller;

import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.CreateFavoriteRequest;
import com.umc.movie.dto.FavoriteResponse;
import com.umc.movie.dto.FavoriteStatusResponse;
import com.umc.movie.interceptor.AuthInterceptor;
import com.umc.movie.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "즐겨찾기", description = "로그인한 회원의 영화 즐겨찾기 API")
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "즐겨찾기 등록", description = "토큰이 필요합니다. 로그인한 회원의 즐겨찾기에 영화를 추가합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "COMMON201: 즐겨찾기 등록 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": true,
                              "code": "COMMON201",
                              "message": "즐겨찾기를 등록했습니다.",
                              "result": {
                                "favoriteId": 1,
                                "movieId": 550,
                                "createdAt": "2026-11-25T21:00:00"
                              }
                            }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "COMMON4001: 영화 ID 입력값 오류",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "AUTH401_2: 토큰 누락, AUTH401_3: 유효하지 않은 토큰, AUTH401_4: 만료된 토큰",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": false,
                              "code": "AUTH401_2",
                              "message": "로그인이 필요한 요청입니다.",
                              "result": null
                            }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "MEMBER404_1: 존재하지 않는 회원",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "FAVORITE409_1: 이미 즐겨찾기한 영화",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": false,
                              "code": "FAVORITE409_1",
                              "message": "이미 이 영화를 즐겨찾기하셨습니다.",
                              "result": null
                            }
                            """))
            )
    })
    @PostMapping("/favorites")
    public ResponseEntity<ApiResponse<FavoriteResponse>> create(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody CreateFavoriteRequest request
    ) {
        FavoriteResponse response = favoriteService.create(memberId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("즐겨찾기를 등록했습니다.", response));
    }

    @Operation(summary = "내 즐겨찾기 조회", description = "토큰이 필요합니다. 로그인한 회원의 즐겨찾기 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "COMMON200: 즐겨찾기 목록 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "AUTH401_2: 토큰 누락, AUTH401_3: 유효하지 않은 토큰, AUTH401_4: 만료된 토큰",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getMyFavorites(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        List<FavoriteResponse> response = favoriteService.getMyFavorites(memberId);
        return ResponseEntity.ok(ApiResponse.ok("내 즐겨찾기 목록을 조회했습니다.", response));
    }

    @Operation(summary = "즐겨찾기 여부 조회", description = "토큰이 필요합니다. 해당 영화의 즐겨찾기 여부를 조회하며, 등록하지 않았다면 isFavorite은 false입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "COMMON200: 즐겨찾기 여부 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "AUTH401_2: 토큰 누락, AUTH401_3: 유효하지 않은 토큰, AUTH401_4: 만료된 토큰",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/favorites/{movieId}")
    public ResponseEntity<ApiResponse<FavoriteStatusResponse>> getStatus(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable("movieId") Long movieId
    ) {
        FavoriteStatusResponse response = favoriteService.getStatus(memberId, movieId);
        return ResponseEntity.ok(ApiResponse.ok("즐겨찾기 여부를 조회했습니다.", response));
    }

    @Operation(summary = "즐겨찾기 해제", description = "토큰이 필요합니다. 영화 ID를 이용해 로그인한 회원의 즐겨찾기를 해제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "COMMON200: 즐겨찾기 해제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "AUTH401_2: 토큰 누락, AUTH401_3: 유효하지 않은 토큰, AUTH401_4: 만료된 토큰",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "FAVORITE404_1: 즐겨찾기하지 않은 영화",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @DeleteMapping("/favorites/{movieId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable("movieId") Long movieId
    ) {
        favoriteService.delete(memberId, movieId);
        return ResponseEntity.ok(ApiResponse.ok("즐겨찾기를 해제했습니다.", null));
    }
}