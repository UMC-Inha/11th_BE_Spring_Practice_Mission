package com.example.springboot_mission.movie.controller;

import com.example.springboot_mission.global.response.ApiResponse;
import com.example.springboot_mission.movie.dto.*;
import com.example.springboot_mission.movie.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/nickname/{nickname}")
    public NicknameCheckResponse checkNickname(
            @PathVariable @Size(min = 2, max = 12) String nickname
    ) {
        return memberService.checkNickname(nickname);
    }

    @GetMapping("/email/{email}")
    public EmailCheckResponse checkEmail(
            @PathVariable @Email String email
    ) {
        return memberService.checkEmail(email);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
            ) {
        MemberResponse response = memberService.signUp(request);

        return ResponseEntity.status(201).body(ApiResponse.created("회원가입에 성공했습니다", response));
    }
}