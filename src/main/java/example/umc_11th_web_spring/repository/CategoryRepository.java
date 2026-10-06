package example.umc_11th_web_spring.repository;

import example.umc_11th_web_spring.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
