package com.example.springboot_mission.movie.controller;

import com.example.springboot_mission.global.util.JwtUtil;
import com.example.springboot_mission.movie.dto.CreateRatingRequest;
import com.example.springboot_mission.movie.entity.Member;
import com.example.springboot_mission.movie.entity.Rating;
import com.example.springboot_mission.movie.repository.MemberRepository;
import com.example.springboot_mission.movie.repository.RatingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RatingApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Member member;
    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        // 테스트 전용 DB에 회원을 저장하고 실제 JwtUtil로 토큰 발급
        member = memberRepository.save(Member.builder()
                .email("rating.challenger@umc.com")
                .password("로그인 검증에 사용하지 않는 테스트 비밀번호")
                .nickname("평점테스트")
                .build());

        authorizationHeader = "Bearer " + jwtUtil.createAccessToken(member.getMemberId(), member.getEmail());
    }

    @Test
    @DisplayName("토큰 없이 평점을 등록하면 401과 AUTH4012를 응답하고 저장하지 않는다")
    void createRatingWithoutToken() throws Exception {
        // given: 유효한 요청 바디는 있지만 토큰은 없는 상황
        CreateRatingRequest request = new CreateRatingRequest(550L, 5, "다시 보고 싶은 영화입니다.");
        long countBefore = ratingRepository.count();

        // when & then: 인증 오류와 공통 실패 응답 확인
        mockMvc.perform(post("/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("AUTH4012"))
                .andExpect(jsonPath("$.message").value("로그인이 필요한 요청입니다."))
                .andExpect(jsonPath("$.result").value(nullValue()));

        assertThat(ratingRepository.count()).isEqualTo(countBefore);
    }

    @Test
    @DisplayName("토큰을 담아 평점을 등록하면 201을 응답하고 토큰의 회원에게 저장한다")
    void createRating() throws Exception {
        // given: 로그인한 회원의 토큰과 평점 등록 요청
        CreateRatingRequest request = new CreateRatingRequest(550L, 5, "다시 보고 싶은 영화입니다.");

        // when & then: 요청에는 memberId 없이 토큰만 전달
        MvcResult result = mockMvc.perform(post("/ratings")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON201"))
                .andExpect(jsonPath("$.result.ratingId").isNumber())
                .andExpect(jsonPath("$.result.movieId").value(550))
                .andExpect(jsonPath("$.result.score").value(5))
                .andExpect(jsonPath("$.result.comment").value(request.comment()))
                .andReturn();

        // 응답뿐 아니라 실제 저장된 평점과 작성자도 확인
        long ratingId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("result").path("ratingId").asLong();
        Rating savedRating = ratingRepository.findById(ratingId).orElseThrow();
        assertThat(savedRating.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedRating.getMovieId()).isEqualTo(request.movieId());
        assertThat(savedRating.getScore()).isEqualTo(request.score());
        assertThat(savedRating.getComment()).isEqualTo(request.comment());
    }
}
