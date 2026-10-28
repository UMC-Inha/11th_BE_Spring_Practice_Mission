package example.umc_11th_web_spring.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findAllByCategoryId(Long categoryId){
        String sql = "SELECT * FROM book WHERE category_id = ?";
        return jdbcTemplate.queryForList(sql, categoryId);
    }

    public void saveRental(Long userId, Long bookId){
        String sql ="INSERT INTO rental (user_id, book_id, rented_at, due_at) VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY))";

        jdbcTemplate.update(sql, userId, bookId);
    }

    public void updateRental(Long rentalId) {
        String sql ="UPDATE rental SET returned_at = NOW() WHERE rental_id = ?";
        jdbcTemplate.update(sql, rentalId);
    }
}