package com.umc.movie.dto.res;

public record MovieRecordResponse(
        RatingResponse rating,
        FavoriteResponse favorite
) {
}
