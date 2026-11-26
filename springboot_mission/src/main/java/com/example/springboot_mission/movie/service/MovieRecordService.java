package com.example.springboot_mission.movie.service;

import com.example.springboot_mission.global.exception.ProjectException;
import com.example.springboot_mission.movie.code.FavoriteErrorCode;
import com.example.springboot_mission.movie.code.MemberErrorCode;
import com.example.springboot_mission.movie.code.RatingErrorCode;
import com.example.springboot_mission.movie.dto.CreateMovieRecordRequest;
import com.example.springboot_mission.movie.dto.FavoriteResponse;
import com.example.springboot_mission.movie.dto.MovieRecordResponse;
import com.example.springboot_mission.movie.dto.RatingResponse;
import com.example.springboot_mission.movie.entity.Favorite;
import com.example.springboot_mission.movie.entity.Member;
import com.example.springboot_mission.movie.entity.Rating;
import com.example.springboot_mission.movie.repository.FavoriteRepository;
import com.example.springboot_mission.movie.repository.MemberRepository;
import com.example.springboot_mission.movie.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                .orElseThrow(() -> new ProjectException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (ratingRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())) {
            throw new ProjectException(RatingErrorCode.RATING_ALREADY_EXISTS);
        }

        if (request.addToFavorites()
                && favoriteRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())) {
            throw new ProjectException(FavoriteErrorCode.ALREADY_FAVORITE);
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
