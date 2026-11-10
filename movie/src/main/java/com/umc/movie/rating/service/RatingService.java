package com.umc.movie.rating.service;

import com.umc.movie.member.entity.Member;
import com.umc.movie.rating.dto.request.CreateRatingRequest;
import com.umc.movie.rating.dto.request.UpdateRatingRequest;
import com.umc.movie.rating.dto.response.RatingResponse;
import com.umc.movie.rating.entity.Rating;
import com.umc.movie.rating.repository.RatingRepository;
import com.umc.movie.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다.");
        }

        return ratingRepository.findByMember_MemberIdOrderByRatingIdDesc(memberId).stream()
                .map(RatingResponse::from)
                .toList();
    }

    @Transactional
    public Long create(Long memberId, CreateRatingRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        // rating 테이블에 (member_id, movie_id) 복합 UNIQUE 제약이 걸려 있음
        // 그냥 저장해도 DB가 막아주지먄, 내가 정한 메세지로 응답하기 위해 먼저 확인
        if(ratingRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())){
            throw new IllegalArgumentException("이미 이 영화에 평점을 남기셨습니다.");
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 평점입니다."));

        if(!rating.getMember().getMemberId().equals(memberId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.");
        }

        return rating;
    }
}