package example.umc_11th_web_spring.service;

import example.umc_11th_web_spring.dto.BookRequest;
import example.umc_11th_web_spring.dto.BookResponse;
import example.umc_11th_web_spring.entity.Book;
import example.umc_11th_web_spring.entity.Category;
import example.umc_11th_web_spring.repository.BookRepository;
import example.umc_11th_web_spring.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<BookResponse> getBooks() {
        return bookRepository.findAllByOrderByBookIdDesc().stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional
    public BookResponse createBook(BookRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        Book book = new Book(category, request.title(), request.description());

        return BookResponse.from(bookRepository.save(book));
    }

//    public List<Map<String, Object>> getAllBooks() {
//        return bookRepository.findAll();
//    }
//
//    public void createBook(Map<String, Object> body) {
//        bookRepository.save(body);
//    }
//
//    public List<Map<String, Object>> getAllBooksByCategoryId(Long categoryId) {
//        return bookRepository.findAllByCategory(categoryId);
//    }
//
//    public void createRental(Map<String, Object> body) {
//        bookRepository.saveRental(body);
//    }
//
//    public void updateRental(Long rentalId) {
//        bookRepository.updateRental(rentalId);
//    }
}
