package com.example.springboot_mission.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMovieRecordRequest(
        @Schema(description = "기록할 TMDB 영화 ID (양수, 필수)", example = "550")
        @NotNull(message = "영화 ID는 필수입니다.")
        @Positive(message = "영화 ID는 양수여야 합니다.")
        Long movieId,

        @Schema(description = "별점 (1~5점, 필수)", example = "5")
        @NotNull(message = "별점은 필수입니다.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 5점 이하여야 합니다.")
        Integer score,

        @Schema(description = "영화 후기 (선택)", example = "평점과 즐겨찾기를 함께 기록합니다.")
        String comment,

        @Schema(description = "즐겨찾기 동시 저장 여부 (필수). false이면 평점만 저장", example = "true")
        @NotNull(message = "즐겨찾기 추가 여부는 필수입니다.")
        Boolean addToFavorites
) {
}
