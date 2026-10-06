package com.umc.study.domain.book.repository;

import com.umc.study.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // GET /books : 최신 등록순 전체 조회
    List<Book> findAllByOrderByBookIdDesc();

    // 카테고리별 조회 (기존 findByCategoryId 대체)
    List<Book> findByCategory_CategoryId(Long categoryId);
}