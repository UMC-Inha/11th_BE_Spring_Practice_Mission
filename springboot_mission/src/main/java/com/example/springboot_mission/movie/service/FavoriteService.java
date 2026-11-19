package com.example.springboot_mission.movie.service;

import com.example.springboot_mission.global.exception.ProjectException;
import com.example.springboot_mission.movie.code.FavoriteErrorCode;
import com.example.springboot_mission.movie.code.MemberErrorCode;
import com.example.springboot_mission.movie.dto.CreateFavoriteRequest;
import com.example.springboot_mission.movie.dto.FavoriteResponse;
import com.example.springboot_mission.movie.dto.FavoriteStatusResponse;
import com.example.springboot_mission.movie.entity.Favorite;
import com.example.springboot_mission.movie.entity.Member;
import com.example.springboot_mission.movie.repository.FavoriteRepository;
import com.example.springboot_mission.movie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FavoriteResponse create(Long memberId, CreateFavoriteRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (favoriteRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())) {
            throw new ProjectException(FavoriteErrorCode.ALREADY_FAVORITE);
        }

        Favorite favorite = Favorite.builder()
                .member(member)
                .movieId(request.movieId())
                .build();

        return FavoriteResponse.from(favoriteRepository.save(favorite));
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getMyFavorites(Long memberId) {
        return favoriteRepository.findByMember_MemberIdOrderByFavoriteIdDesc(memberId).stream()
                .map(FavoriteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FavoriteStatusResponse getStatus(Long memberId, Long movieId) {
        boolean isFavorite = favoriteRepository.existsByMember_MemberIdAndMovieId(memberId, movieId);
        return new FavoriteStatusResponse(isFavorite);
    }

    @Transactional
    public void delete(Long memberId, Long movieId) {
        Favorite favorite = favoriteRepository.findByMember_MemberIdAndMovieId(memberId, movieId)
                .orElseThrow(() -> new ProjectException(FavoriteErrorCode.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);
    }
}