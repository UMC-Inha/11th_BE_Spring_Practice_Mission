// src/main/java/.../repository/BookRepository.java
package example.umc_11th_web_spring.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository // 스프링 컨테이너에 "나 창고지기 부품이야!"라고 등록
@RequiredArgsConstructor
public class BookRepository {

    // 2단계에서 준비된 스프링의 DB 통신 도구(JdbcTemplate) 주입
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM book";

        // 쿼리를 실행하고 결과를 List<Map> 형태의 날것 데이터로 긁어옵니다.
        // Map의 Key는 '컬럼명(title)', Value는 '실제 데이터(달빛 도서관)'가 됩니다.
        return jdbcTemplate.queryForList(sql);
    }

    public void save(Map<String, Object> body){
        // book_id는 AUTO_INCREMENT이므로 생략, is_available은 기본 true로 삽입
        String sql = "INSERT INTO book (category_id, title, description, is_available) VALUES (?, ?, ?, true)";

        // SQL 뒤에 파라미터를 차례대로 넘겨주면 ? 자리에 순서대로 안전하게 바인딩됩니다.
        jdbcTemplate.update(
                sql,
                body.get("categoryId"),
                body.get("title"),
                body.get("description")
        );
    }

    // 카테고리별 도서 조회 쿼리문 실행
    public List<Map<String, Object>> findBooksByCategoryId(Long categoryId) {
        // 1. ? 바인딩 문법을 사용한 생 SQL 쿼리 정의
        String sql = "SELECT * FROM book WHERE category_id = ?";

        // 2. ? 자리에 categoryId 값을 채워 넣어 MySQL에 쿼리를 전송합니다.
        // 조회된 레코드 목록을 List<Map<컬럼명, 데이터값>> 형태로 변환하여 반환합니다.
        return jdbcTemplate.queryForList(sql, categoryId);
    }
}

