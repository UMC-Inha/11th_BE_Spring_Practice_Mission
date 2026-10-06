package com.umc.study.repository;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    // db 연결, 쿼리 날리기, 정리 등등 편하게 해줌
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findAll(){
        String sql = "SELECT * FROM book";

        return jdbcTemplate.queryForList(sql);
    }

    public void save(Map<String,Object> body){
        String sql = "INSERT INTO book (category_id, title, description, is_available) VALUES (?, ?, ?, true)";

        jdbcTemplate.update(
                sql,
                body.get("categoryId"),
                body.get("title"),
                body.get("description")
        );
    }

    //미션 1: 특정 카테고리 도서 목록 조회 API 구현
    public List<Map<String, Object>> findByCategoryId(Long categoryId) {
        String sql = "SELECT * FROM book WHERE category_id = ?";

        return jdbcTemplate.queryForList(
                sql,
                categoryId
        );
    }
}
