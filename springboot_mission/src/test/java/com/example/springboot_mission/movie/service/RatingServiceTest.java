package com.example.springboot_mission.movie.service;

import com.example.springboot_mission.global.exception.ProjectException;
import com.example.springboot_mission.movie.code.RatingErrorCode;
import com.example.springboot_mission.movie.dto.CreateRatingRequest;
import com.example.springboot_mission.movie.dto.UpdateRatingRequest;
import com.example.springboot_mission.movie.entity.Member;
import com.example.springboot_mission.movie.entity.Rating;
import com.example.springboot_mission.movie.repository.MemberRepository;
import com.example.springboot_mission.movie.repository.RatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
    @DisplayName("같은 영화에 평점을 중복 등록하면 RATING4091 예외를 던지고 저장하지 않는다")
    void createRatingWhenDuplicated() {
        // given: 회원이 같은 영화에 이미 평점을 남긴 상황
        Member member = Member.builder()
                .email("challenger@umc.com")
                .password("암호화된비밀번호")
                .nickname("영화조아")
                .build();
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 550L)).willReturn(true);

        // when & then: 중복 오류가 나고 저장은 시도하지 않는다
        assertThatThrownBy(() -> ratingService.create(1L, new CreateRatingRequest(550L, 5, "좋은 영화")))
                .isInstanceOfSatisfying(ProjectException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(RatingErrorCode.RATING_ALREADY_EXISTS);
                    assertThat(exception.getErrorCode().getCode()).isEqualTo("RATING4091");
                    assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                });

        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("남의 평점을 수정하면 RATING4031 예외를 던지고 원래 평점을 유지한다")
    void updateRatingWhenNotOwner() {
        // given: 1번 회원의 평점을 2번 회원이 수정하려는 상황
        Member owner = mock(Member.class);
        given(owner.getMemberId()).willReturn(1L);
        Rating rating = Rating.builder()
                .member(owner)
                .movieId(550L)
                .score(5)
                .comment("원래 후기")
                .build();
        given(ratingRepository.findById(10L)).willReturn(Optional.of(rating));

        // when & then: 권한 오류가 나고 별점·후기와 저장 호출 모두 변경하지 않는다
        assertThatThrownBy(() -> ratingService.update(2L, 10L, new UpdateRatingRequest(1, "다른 회원의 수정")))
                .isInstanceOfSatisfying(ProjectException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(RatingErrorCode.NOT_RATING_OWNER);
                    assertThat(exception.getErrorCode().getCode()).isEqualTo("RATING4031");
                    assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                });

        assertThat(rating.getScore()).isEqualTo(5);
        assertThat(rating.getComment()).isEqualTo("원래 후기");
        verify(ratingRepository, never()).save(any(Rating.class));
    }
}
