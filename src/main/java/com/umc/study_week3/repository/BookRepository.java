// src/main/java/.../repository/BookRepository.java
package com.umc.study_week3.repository;

import com.umc.study_week3.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// SQL 문자열 없이, 메서드 이름만으로 Spring Data JPA가 쿼리를 만들어 줍니다.
public interface BookRepository extends JpaRepository<Book, Long> {

    // SELECT * FROM book ORDER BY book_id DESC
    List<Book> findAllByOrderByBookIdDesc();

    // SELECT * FROM book WHERE category_id = ? ORDER BY book_id DESC
    List<Book> findAllByCategory_CategoryIdOrderByBookIdDesc(Long categoryId);
}
