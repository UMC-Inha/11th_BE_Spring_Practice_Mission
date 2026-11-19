package com.example.springboot_mission.movie.dto;

public record MovieRecordResponse(
        RatingResponse rating,
        FavoriteResponse favorite
) {
}