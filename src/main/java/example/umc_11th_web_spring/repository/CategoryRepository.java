package example.umc_11th_web_spring.repository;

import example.umc_11th_web_spring.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findById(Long categoryId);
}
