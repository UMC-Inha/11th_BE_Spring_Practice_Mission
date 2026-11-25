// src/main/java/com/umc/movie/controller/MemberController.java
package com.umc.movie.controller;

import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.MemberInfoResponse;
import com.umc.movie.dto.MemberResponse;
import com.umc.movie.dto.MemberUpdateRequest;
import com.umc.movie.dto.SignUpRequest;
import com.umc.movie.interceptor.AuthInterceptor;
import com.umc.movie.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원", description = "회원가입과 내 정보 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 닉네임으로 회원을 등록합니다. 토큰이 없어도 호출할 수 있는 공개 API입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": true,
                              "code": "COMMON201",
                              "message": "회원가입에 성공했습니다.",
                              "result": { "memberId": 1, "email": "user@example.com", "nickname": "영화조아" }
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": false,
                              "code": "COMMON400",
                              "message": "올바른 이메일 형식을 입력해주세요.",
                              "result": null
                            }
                            """)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        MemberResponse response = memberService.signUp(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("회원가입에 성공했습니다.", response));
    }

    @Operation(
            summary = "내 정보 조회",
            description = "토큰의 주인인 회원의 이메일, 닉네임, 가입일을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "내 정보 조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": true,
                              "code": "COMMON200",
                              "message": "내 정보를 조회했습니다.",
                              "result": { "email": "user@example.com", "nickname": "영화조아", "createdAt": "2026-09-13T21:00:00" }
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "토큰이 없거나 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": false,
                              "code": "AUTH401_2",
                              "message": "로그인이 필요한 요청입니다.",
                              "result": null
                            }
                            """)))
    })
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("내 정보를 조회했습니다.", memberService.getMyInfo(memberId)));
    }

    @Operation(
            summary = "내 정보 수정",
            description = "토큰의 주인인 회원의 닉네임을 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "내 정보 수정 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": true,
                              "code": "COMMON200",
                              "message": "내 정보를 수정했습니다.",
                              "result": { "email": "user@example.com", "nickname": "새닉네임", "createdAt": "2026-09-13T21:00:00" }
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "토큰이 없거나 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": false,
                              "code": "AUTH401_2",
                              "message": "로그인이 필요한 요청입니다.",
                              "result": null
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "이미 사용 중인 닉네임",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": false,
                              "code": "MEMBER409_1",
                              "message": "이미 사용 중인 닉네임입니다.",
                              "result": null
                            }
                            """)))
    })
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid MemberUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("내 정보를 수정했습니다.", memberService.updateMyInfo(memberId, request)));
    }
}
