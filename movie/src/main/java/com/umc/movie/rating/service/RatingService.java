package com.umc.movie.rating.service;

import com.umc.movie.common.exception.ApiException;
import com.umc.movie.common.exception.ErrorCode;
import com.umc.movie.member.entity.Member;
import com.umc.movie.rating.dto.request.CreateRatingRequest;
import com.umc.movie.rating.dto.request.UpdateRatingRequest;
import com.umc.movie.rating.dto.response.RatingResponse;
import com.umc.movie.rating.entity.Rating;
import com.umc.movie.rating.repository.RatingRepository;
import com.umc.movie.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return ratingRepository.findByMember_MemberIdOrderByRatingIdDesc(memberId).stream()
                .map(RatingResponse::from)
                .toList();
    }

    @Transactional
    public Long create(Long memberId, CreateRatingRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        // rating 테이블에 (member_id, movie_id) 복합 UNIQUE 제약이 걸려 있음
        // 그냥 저장해도 DB가 막아주지먄, 내가 정한 메세지로 응답하기 위해 먼저 확인
        if(ratingRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())){
            throw new ApiException(ErrorCode.RATING_DUPLICATED);
        }

        Rating rating = Rating.builder()
                .member(member)
                .movieId(request.movieId())
                .score(request.score())
                .comment(request.comment())
                .build();

        return ratingRepository.save(rating).getRatingId();
    }

    @Transactional
    public void update(Long ratingId, Long memberId, UpdateRatingRequest request){
        Rating rating = findOwnedRating(ratingId, memberId);

        rating.update(request.score(), request.comment());
        ratingRepository.save(rating);
    }

    @Transactional
    public void delete(Long ratingId, Long memberId){
        Rating rating = findOwnedRating(ratingId, memberId);

        ratingRepository.delete(rating);
    }

    /**
     * 평점을 찾고, 그 평점이 요청자의 것이지 확인
     * 먼저 찾아보고(없으면 404), 찾았으면 찾은 Rating reurn
     * */
    private Rating findOwnedRating(Long ratingId, Long memberId){
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ApiException(ErrorCode.RATING_NOT_FOUND));

        if(!rating.getMember().getMemberId().equals(memberId)){
            throw new ApiException(ErrorCode.RATING_FORBIDDEN);
        }

        return rating;
    }
}