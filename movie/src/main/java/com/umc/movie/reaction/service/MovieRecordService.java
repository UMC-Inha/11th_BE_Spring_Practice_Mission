package com.umc.movie.reaction.service;

import com.umc.movie.common.exception.ApiException;
import com.umc.movie.common.exception.ErrorCode;
import com.umc.movie.favorite.dto.response.FavoriteResponse;
import com.umc.movie.favorite.entity.Favorite;
import com.umc.movie.favorite.repository.FavoriteRepository;
import com.umc.movie.member.entity.Member;
import com.umc.movie.member.repository.MemberRepository;
import com.umc.movie.rating.dto.response.RatingResponse;
import com.umc.movie.rating.entity.Rating;
import com.umc.movie.rating.repository.RatingRepository;
import com.umc.movie.reaction.dto.request.CreateMovieRecordRequest;
import com.umc.movie.reaction.dto.response.MovieRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieRecordService {
    private final MemberRepository memberRepository;
    private final RatingRepository ratingRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public MovieRecordResponse create(Long memberId, CreateMovieRecordRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        if (ratingRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())) {
            throw new ApiException(ErrorCode.RATING_DUPLICATED);
        }

        if (request.addToFavorites()
                && favoriteRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())) {
            throw new ApiException(ErrorCode.FAVORITE_DUPLICATED);
        }

        Rating rating = Rating.builder()
                .member(member)
                .movieId(request.movieId())
                .score(request.score())
                .comment(request.comment())
                .build();
        Rating savedRating = ratingRepository.save(rating);

        FavoriteResponse favoriteResponse = null;
        if (request.addToFavorites()) {
            Favorite favorite = Favorite.builder()
                    .member(member)
                    .movieId(request.movieId())
                    .build();
            Favorite savedFavorite = favoriteRepository.save(favorite);
            favoriteResponse = FavoriteResponse.from(savedFavorite);
        }

        return new MovieRecordResponse(
                RatingResponse.from(savedRating),
                favoriteResponse
        );
    }
}
