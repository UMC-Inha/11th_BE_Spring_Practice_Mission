package example.umc_11th_web_spring.repository;

import example.umc_11th_web_spring.entity.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {
    @EntityGraph(attributePaths = "category")
    List<Book> findAllByOrderByBookIdDesc();
}
