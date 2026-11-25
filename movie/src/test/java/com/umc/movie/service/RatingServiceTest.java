// src/test/java/com/umc/movie/service/RatingServiceTest.java
package com.umc.movie.service;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RatingService ratingService;

    private final Member member = Member.builder()
            .email("challenger@umc.com")
            .password("암호화된비밀번호")
            .nickname("영화조아")
            .build();


    @Test
    @DisplayName("같은 영화에 평점을 중복 등록하면 RATING409_1 예외를 던진다")
    void createRatingWhenDuplicated() {
        // given: 회원은 존재하고, 같은 영화에 이미 평점을 남긴 상황
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 550L)).willReturn(true);

        // when & then: 예외가 나고 에러 코드가 RATING409_1이다
        assertThatThrownBy(() -> ratingService.create(1L, new CreateRatingRequest(550L, 4, "좋은 영화")))
                .isInstanceOf(ApiException.class)
                .hasMessage("이미 이 영화에 평점을 남기셨습니다.");
    }

    @Test
    @DisplayName("남의 평점을 수정하려 하면 RATING403_1 예외를 던진다")
    void updateRatingOfOtherMember() {
        // given: ratingId=1 평점의 주인은 memberId=2인 다른 회원인 상황
        // Builder로 만든 Member는 memberId가 null이라 NPE 발생 → mock으로 ID 고정
        Member otherMember = mock(Member.class);
        given(otherMember.getMemberId()).willReturn(2L);

        Rating othersRating = Rating.builder()
                .member(otherMember)
                .movieId(550L)
                .score(3)
                .comment("그저 그랬어요")
                .build();
        given(ratingRepository.findById(1L)).willReturn(Optional.of(othersRating));

        // when & then: memberId=1인 회원이 수정 요청하면 403 예외가 난다
        assertThatThrownBy(() -> ratingService.update(1L, 1L, new UpdateRatingRequest(5, "수정 시도")))
                .isInstanceOf(ApiException.class)
                .hasMessage("본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.");
    }
}
