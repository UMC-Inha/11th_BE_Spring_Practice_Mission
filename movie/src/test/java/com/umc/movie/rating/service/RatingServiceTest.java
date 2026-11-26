package com.umc.movie.rating.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.umc.movie.common.exception.ApiException;
import com.umc.movie.member.entity.Member;
import com.umc.movie.member.repository.MemberRepository;
import com.umc.movie.rating.dto.request.CreateRatingRequest;
import com.umc.movie.rating.dto.request.UpdateRatingRequest;
import com.umc.movie.rating.entity.Rating;
import com.umc.movie.rating.repository.RatingRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class) // @Mock이 붙은 가짜 객체를 만들어줌
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks // 위의 가짜 객체 2개를 주입받은 진짜 객체임
    private RatingService ratingService;

    // 엔티티의 id는 DB가 채워주므로, 단위 테스트에서는 리플렉션으로 직접 넣음
    private Member memberWithId(Long memberId, String email, String nickname) {
        Member member = Member.builder()
                .email(email)
                .password("암호화된비밀번호")
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(member, "memberId", memberId);
        return member;
    }

    @Test
    @DisplayName("같은 영화에 평점을 중복 등록하면 409와 RATING409_1 예외를 던지고 저장하지 않는다")
    void createRatingWhenDuplicated() {
        // given: 회원은 존재하고, 이 영화에 이미 평점을 남긴 상황임
        Member member = memberWithId(1L, "challenger@umc.com", "영화조아");
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 550L)).willReturn(true);

        // when & then: 예외가 나고, 저장은 시도하지 않음
        assertThatThrownBy(() -> ratingService.create(1L, new CreateRatingRequest(550L, 5, "재밌어요")))
                .isInstanceOfSatisfying(ApiException.class, e -> {
                    assertThat(e.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(e.getCode()).isEqualTo("RATING409_1");
                });

        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("남의 평점을 수정하려 하면 403과 RATING403_1 예외를 던지고 수정하지 않는다")
    void updateRatingOfOtherMember() {
        // given: 1번 회원의 평점이 있고, 2번 회원이 그 평점을 수정하려는 상황임
        Member owner = memberWithId(1L, "owner@umc.com", "주인");
        Rating rating = Rating.builder()
                .member(owner)
                .movieId(550L)
                .score(5)
                .comment("재밌어요")
                .build();
        given(ratingRepository.findById(10L)).willReturn(Optional.of(rating));

        // when & then: 주인이 아니므로 예외가 나고, 저장은 시도하지 않음
        assertThatThrownBy(() -> ratingService.update(10L, 2L, new UpdateRatingRequest(1, "별로예요")))
                .isInstanceOfSatisfying(ApiException.class, e -> {
                    assertThat(e.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(e.getCode()).isEqualTo("RATING403_1");
                });

        verify(ratingRepository, never()).save(any(Rating.class));
        // 평점 내용도 그대로여야 함
        assertThat(rating.getScore()).isEqualTo(5);
    }
}
