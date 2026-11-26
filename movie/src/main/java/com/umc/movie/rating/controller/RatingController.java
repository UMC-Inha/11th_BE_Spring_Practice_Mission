package com.umc.movie.rating.controller;

import com.umc.movie.auth.interceptor.AuthInterceptor;
import com.umc.movie.common.response.ApiResponse;
import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.rating.dto.request.CreateRatingRequest;
import com.umc.movie.rating.dto.request.UpdateRatingRequest;
import com.umc.movie.rating.dto.response.RatingResponse;
import com.umc.movie.rating.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "평점", description = "영화 평점 등록, 수정, 삭제, 조회 API")
public class RatingController {

    private final RatingService ratingService;

    @Operation(
            summary = "유저별 평점 조회",
            description = "특정 회원이 남긴 평점 목록을 최신순으로 조회함. 토큰 없이 호출할 수 있는 공개 API임. 응답은 공통 응답으로 감싸지 않고 평점 배열이 바로 내려감."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MEMBER404_1 - 존재하지 않는 회원")
    })
    //공개 api라 토큰을 받지 않음
    @GetMapping("/members/{memberId}/ratings")
    public List<RatingResponse> getMemberRatings(@PathVariable Long memberId) {
        return ratingService.getRatingsByMember(memberId);
    }

    @Operation(
            summary = "평점 등록",
            description = "토큰의 주인인 회원이 영화 하나에 별점(1~5)과 후기를 남김. 토큰이 필요함."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH) // 토큰이 필요한 API임
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "평점 등록 성공. result에 ratingId가 내려감"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "COMMON4001 - 요청값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "AUTH401_2 - 토큰이 없음 / AUTH401_3 - 유효하지 않은 토큰 / AUTH401_4 - 만료된 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MEMBER404_1 - 존재하지 않는 회원"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "RATING409_1 - 이미 이 영화에 평점을 남김")
    })
    // /members/{memberId}가 사라지고 인터셉터가 담아둔 memberId값을 쓴다
    @PostMapping("/ratings")
    public ResponseEntity<ApiResponse<Long>> create(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody CreateRatingRequest request
    ){
        Long ratingId = ratingService.create(memberId, request);
        return ResponseEntity.status(201).body(ApiResponse.created("평점 등록에 성공했습니다.", ratingId));
    }

    @Operation(
            summary = "평점 수정",
            description = "내가 남긴 평점의 별점과 후기를 수정함. 본인 평점만 수정할 수 있음. 토큰이 필요함."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH) // 토큰이 필요한 API임
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평점 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "COMMON4001 - 요청값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "AUTH401_2 - 토큰이 없음 / AUTH401_3 - 유효하지 않은 토큰 / AUTH401_4 - 만료된 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "RATING403_1 - 다른 회원의 평점"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RATING404_1 - 존재하지 않는 평점")
    })
    @PatchMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long ratingId,
            @Valid @RequestBody UpdateRatingRequest request
    ){
        ratingService.update(ratingId, memberId, request);
        return ResponseEntity.ok(ApiResponse.ok("평점 수정에 성공했습니다.", null));
    }

    @Operation(
            summary = "평점 삭제",
            description = "내가 남긴 평점을 삭제함. 본인 평점만 삭제할 수 있음. 토큰이 필요함."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH) // 토큰이 필요한 API임
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "평점 삭제 성공. 응답 본문이 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "AUTH401_2 - 토큰이 없음 / AUTH401_3 - 유효하지 않은 토큰 / AUTH401_4 - 만료된 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "RATING403_1 - 다른 회원의 평점"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RATING404_1 - 존재하지 않는 평점")
    })
    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<Void> delete(@RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId, @PathVariable Long ratingId){
        ratingService.delete(ratingId, memberId);
        return ResponseEntity.noContent().build();
    }
}