package com.example.springboot_mission.movie.controller;

import com.example.springboot_mission.global.response.ApiResponse;
import com.example.springboot_mission.movie.config.SwaggerConfig;
import com.example.springboot_mission.movie.dto.*;
import com.example.springboot_mission.movie.interceptor.AuthInterceptor;
import com.example.springboot_mission.movie.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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
            summary = "닉네임 중복 확인",
            description = "토큰 없이 호출할 수 있는 공개 API입니다. 사용 가능한 닉네임이면 available이 true입니다. 공통 응답으로 감싸지 않은 객체를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "닉네임 중복 확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "available": true
                                    }
                                    """)
                    }))
    })
    @GetMapping("/nickname/{nickname}")
    public NicknameCheckResponse checkNickname(
            @PathVariable @Size(min = 2, max = 12) String nickname
    ) {
        return memberService.checkNickname(nickname);
    }

    @Operation(
            summary = "이메일 중복 확인",
            description = "토큰 없이 호출할 수 있는 공개 API입니다. 사용 가능한 이메일이면 available이 true입니다. 공통 응답으로 감싸지 않은 객체를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "이메일 중복 확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "available": true
                                    }
                                    """)
                    }))
    })
    @GetMapping("/email/{email}")
    public EmailCheckResponse checkEmail(
            @PathVariable @Email String email
    ) {
        return memberService.checkEmail(email);
    }

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 닉네임으로 회원을 등록합니다. 토큰이 없어도 호출할 수 있는 공개 API입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON201",
                                      "message": "회원가입에 성공했습니다",
                                      "result": {
                                        "memberId": 1,
                                        "email": "challenger@umc.com",
                                        "nickname": "영화조아"
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
                                      "message": "올바른 이메일 형식이 아닙니다.",
                                      "result": null
                                    }
                                    """)
                    })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "이메일·닉네임 중복 또는 DB 제약 위반",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "이메일 중복", value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH4091",
                                      "message": "이미 가입된 이메일입니다.",
                                      "result": null
                                    }
                                    """),
                            @ExampleObject(name = "닉네임 중복", value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH4092",
                                      "message": "이미 존재하는 닉네임입니다.",
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
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
            ) {
        MemberResponse response = memberService.signUp(request);

        return ResponseEntity.status(201).body(ApiResponse.created("회원가입에 성공했습니다", response));
    }

    @Operation(
            summary = "내 정보 조회",
            description = "Bearer 토큰이 필요한 API입니다. 토큰의 회원 정보를 조회합니다. memberId를 URL이나 요청 바디로 보내지 않습니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "내 정보 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "회원정보를 조회했습니다.",
                                      "result": {
                                        "email": "challenger@umc.com",
                                        "nickname": "영화조아",
                                        "createdAt": "2026-09-13T21:00:00"
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
                    }))
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMyInfo(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        MemberInfoResponse response = memberService.getMyInfo(memberId);

        return ResponseEntity.ok(ApiResponse.ok("회원정보를 조회했습니다.", response));
    }

    @Operation(
            summary = "내 정보 수정",
            description = "Bearer 토큰이 필요한 API입니다. 토큰의 회원 닉네임을 수정합니다. 현재 닉네임은 그대로 사용할 수 있지만 다른 회원의 닉네임은 사용할 수 없습니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "내 정보 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "회원 정보를 수정했습니다.",
                                      "result": {
                                        "email": "challenger@umc.com",
                                        "nickname": "영화조아",
                                        "createdAt": "2026-09-13T21:00:00"
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
                                      "message": "닉네임은 2자 이상 12자 이하여야 합니다.",
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
                    responseCode = "409", description = "닉네임 중복 또는 DB 제약 위반",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "닉네임 중복", value = """
                                    {
                                      "isSuccess": false,
                                      "code": "MEMBER4091",
                                      "message": "이미 사용중인 닉네임입니다.",
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
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateMyInfo(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid MemberUpdateRequest request
    ) {
        MemberInfoResponse response = memberService.updateMyInfo(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok("회원 정보를 수정했습니다.", response));
    }
}
