package com.umc.movie.reaction.dto.response;

import com.umc.movie.favorite.dto.response.FavoriteResponse;
import com.umc.movie.rating.dto.response.RatingResponse;

public record MovieRecordResponse(
        RatingResponse rating,
        FavoriteResponse favorite
) {
}
