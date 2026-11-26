package com.umc.movie.member.controller;

import com.umc.movie.auth.interceptor.AuthInterceptor;
import com.umc.movie.common.response.ApiResponse;
import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.member.dto.request.MemberUpdateRequest;
import com.umc.movie.member.dto.request.SignUpRequest;
import com.umc.movie.member.dto.response.EmailCheckResponse;
import com.umc.movie.member.dto.response.MemberInfoResponse;
import com.umc.movie.member.dto.response.MemberResponse;
import com.umc.movie.member.dto.response.NicknameCheckResponse;
import com.umc.movie.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원", description = "회원가입과 내 정보 API")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/nickname/{nickname}")
    public NicknameCheckResponse checkNickname(@PathVariable String nickname) {
        return memberService.checkNickname(nickname);
    }

    @GetMapping("/email/{email}")
    public EmailCheckResponse checkEmail(@PathVariable String email) {
        return memberService.checkEmail(email);
    }

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 닉네임으로 회원을 등록합니다. 토큰이 없어도 호출할 수 있는 공개 API입니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp( @Valid @RequestBody SignUpRequest request){
        MemberResponse response = memberService.signUp(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("회원가입에 성공했습니다.", response));
    }

    @Operation(
            summary = "내 정보 조회",
            description = "토큰의 주인인 회원의 이메일, 닉네임, 가입일을 조회함. 토큰이 필요함."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH) // 토큰이 필요한 API임
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "AUTH401_2 - 토큰이 없음 / AUTH401_3 - 유효하지 않은 토큰 / AUTH401_4 - 만료된 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MEMBER404_1 - 존재하지 않는 회원")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMyInfo(
            //인터셉터가 request.setAttribute("memberId"...)로 담아둔 값을 꺼냄
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ){
        MemberInfoResponse response = memberService.getMyInfo(memberId);
        return ResponseEntity.ok(ApiResponse.ok("회원 정보를 조회했습니다.", response));
    }

    @Operation(
            summary = "내 정보 수정",
            description = "토큰의 주인인 회원의 닉네임을 수정함. 토큰이 필요함."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH) // 토큰이 필요한 API임
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "COMMON4001 - 요청값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "AUTH401_2 - 토큰이 없음 / AUTH401_3 - 유효하지 않은 토큰 / AUTH401_4 - 만료된 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MEMBER404_1 - 존재하지 않는 회원"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "MEMBER409_1 - 이미 사용 중인 닉네임")
    })
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid MemberUpdateRequest request
            ){
        MemberInfoResponse response = memberService.updateMyInfo(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok("회원 정보를 수정했습니다.", response));
    }

}
