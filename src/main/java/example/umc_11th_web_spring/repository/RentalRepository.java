package example.umc_11th_web_spring.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentalRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveRental(Long userId, Long bookId) {
        // 요구사항: rented_at은 NOW(), due_at은 DATE_ADD(NOW(), INTERVAL 7 DAY)
        String sql = "INSERT INTO rental (user_id, book_id, rented_at, due_at) " +
                "VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY))";

        // INSERT 작업은 jdbcTemplate.update()를 사용합니다.
        jdbcTemplate.update(sql, userId, bookId);
    }
}