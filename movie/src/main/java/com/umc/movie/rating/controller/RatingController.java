package com.umc.movie.rating.controller;

import com.umc.movie.auth.interceptor.AuthInterceptor;
import com.umc.movie.common.response.ApiResponse;
import com.umc.movie.rating.dto.request.CreateRatingRequest;
import com.umc.movie.rating.dto.request.UpdateRatingRequest;
import com.umc.movie.rating.dto.response.RatingResponse;
import com.umc.movie.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
// @RequestMapping("/members")
public class RatingController {

    private final RatingService ratingService;

    //공개 api라 토큰을 받지 않음
    @GetMapping("/members/{memberId}/ratings")
    public List<RatingResponse> getMemberRatings(@PathVariable Long memberId) {
        return ratingService.getRatingsByMember(memberId);
    }

    // /members/{memberId}가 사라지고 인터셉터가 담아둔 memberId값을 쓴다
    @PostMapping("/ratings")
    public ResponseEntity<ApiResponse<Long>> create(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody CreateRatingRequest request
    ){
        Long ratingId = ratingService.create(memberId, request);
        return ResponseEntity.status(201).body(ApiResponse.created("평점 등록에 성공했습니다.", ratingId));
    }

    @PatchMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long ratingId,
            @Valid @RequestBody UpdateRatingRequest request
    ){
        ratingService.update(ratingId, memberId, request);
        return ResponseEntity.ok(ApiResponse.ok("평점 수정에 성공했습니다.", null));
    }

    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<Void> delete(@RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId, @PathVariable Long ratingId){
        ratingService.delete(ratingId, memberId);
        return ResponseEntity.noContent().build();
    }
}