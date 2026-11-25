package com.umc.movie.controller;

import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.MemberInfoResponse;
import com.umc.movie.dto.MemberResponse;
import com.umc.movie.dto.MemberUpdateRequest;
import com.umc.movie.dto.NicknameCheckResponse;
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

// 공개 API(회원가입, 중복 체크)와 토큰이 필요한 API(내 정보)가 섞여 있어서
// @SecurityRequirement는 클래스가 아니라 토큰이 필요한 메서드에만 붙입니다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원", description = "회원가입과 내 정보 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "닉네임 중복 체크", description = "사용 가능한 닉네임이면 available이 true입니다. 토큰이 필요 없는 공개 API입니다.")
    @GetMapping("/nickname/{nickname}")
    public NicknameCheckResponse checkNickname(@PathVariable String nickname) {
        return memberService.checkNickname(nickname);
    }

    @Operation(summary = "이메일 중복 체크", description = "사용 가능한 이메일이면 available이 true입니다. 토큰이 필요 없는 공개 API입니다.")
    @GetMapping("/email/{email}")
    public EmailCheckResponse checkEmail(@PathVariable String email) {
        return memberService.checkEmail(email);
    }

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 닉네임으로 회원을 등록합니다. 토큰이 없어도 호출할 수 있는 공개 API입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "요청 값 검증 실패 (COMMON4001)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "COMMON4001", "message": "비밀번호는 대문자/소문자/숫자/특수문자를 포함한 8자 이상이어야 합니다.", "result": null }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "이메일 또는 닉네임 중복 (COMMON4092)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "COMMON4092", "message": "이미 사용 중인 이메일입니다.", "result": null }
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

    @Operation(summary = "내 정보 조회", description = "토큰의 주인인 회원의 이메일, 닉네임, 가입일을 조회합니다. 토큰이 필요합니다.")
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "토큰이 없거나 유효하지 않음 (AUTH401_2, AUTH401_3, AUTH401_4)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "AUTH401_2", "message": "로그인이 필요한 요청입니다.", "result": null }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "회원이 없음 (MEMBER404_1)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "MEMBER404_1", "message": "해당 회원을 찾을 수 없습니다.", "result": null }
                            """)))
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMyInfo(
            // 인터셉터가 request.setAttribute("memberId", ...)로 담아둔 값을 꺼냅니다
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        MemberInfoResponse response = memberService.getMyInfo(memberId);
        return ResponseEntity.ok(ApiResponse.ok("회원 정보를 조회했습니다.", response));
    }

    @Operation(summary = "내 정보 수정", description = "토큰의 주인인 회원의 닉네임을 바꿉니다. 토큰이 필요합니다.")
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "요청 값 검증 실패 (COMMON4001)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "COMMON4001", "message": "닉네임은 2자 이상 12자 이하여야 합니다.", "result": null }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "토큰이 없거나 유효하지 않음 (AUTH401_2, AUTH401_3, AUTH401_4)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "AUTH401_2", "message": "로그인이 필요한 요청입니다.", "result": null }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "다른 회원이 쓰는 닉네임 (MEMBER409_1)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "MEMBER409_1", "message": "이미 사용 중인 닉네임입니다.", "result": null }
                            """)))
    })
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody MemberUpdateRequest request
    ) {
        MemberInfoResponse response = memberService.updateMyInfo(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok("회원 정보를 수정했습니다.", response));
    }
}
