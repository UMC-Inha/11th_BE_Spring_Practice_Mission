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
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    @DisplayName("같은 영화에 평점을 중복 등록하면 409와 RATING409_1 예외를 던지고 저장하지 않는다")
    void createRatingWhenDuplicated() {
        Member member = Member.builder()
                .email("rating@umc.com")
                .password("test-password")
                .nickname("평점회원")
                .build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 550L))
                .willReturn(true);

        ApiException exception = assertThrows(ApiException.class,
                () -> ratingService.create(1L,
                        new CreateRatingRequest(550L, 5, "재미있어요.")));

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getCode()).isEqualTo("RATING409_1");
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("다른 회원의 평점을 수정하면 403과 RATING403_1 예외를 던지고 기존 값을 유지한다")
    void updateRatingWhenNotOwner() {
        Member owner = Member.builder()
                .email("owner@umc.com")
                .password("test-password")
                .nickname("평점작성자")
                .build();
        ReflectionTestUtils.setField(owner, "memberId", 2L);

        Rating rating = Rating.builder()
                .member(owner)
                .movieId(550L)
                .score(3)
                .comment("기존 후기")
                .build();

        given(ratingRepository.findById(10L)).willReturn(Optional.of(rating));

        ApiException exception = assertThrows(ApiException.class,
                () -> ratingService.update(1L, 10L,
                        new UpdateRatingRequest(5, "변경한 후기")));

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(exception.getCode()).isEqualTo("RATING403_1");
        assertThat(rating.getScore()).isEqualTo(3);
        assertThat(rating.getComment()).isEqualTo("기존 후기");
        verify(ratingRepository, never()).saveAndFlush(any(Rating.class));
    }
}