package example.umc_11th_web_spring.service;

import example.umc_11th_web_spring.dto.BookReqDTO;
import example.umc_11th_web_spring.dto.BookResDTO;
import example.umc_11th_web_spring.entity.Book;
import example.umc_11th_web_spring.entity.Category;
import example.umc_11th_web_spring.repository.BookRepository;
import example.umc_11th_web_spring.repository.JdbcBookRepository;
import example.umc_11th_web_spring.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookService {

    private final JdbcBookRepository jdbcBookRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<BookResDTO.BookResponse> getBooks() {
        return bookRepository.findAllByOrderByBookIdDesc().stream()
                .map(BookResDTO.BookResponse::from)
                .toList();
    }

    @Transactional
    public BookResDTO.BookResponse createBook(BookReqDTO.CreateBookRequest request) {

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."));

        Book book = new Book(category,
                request.title(),
                request.description());

        bookRepository.save(book);

        return BookResDTO.BookResponse.from(book);
    }

    public List<Map<String, Object>> getBooksByCategoryId(Long categoryId){
        return  jdbcBookRepository.findAllByCategoryId(categoryId);
    }

    public void createRental(Long userId, Long bookId){
        jdbcBookRepository.saveRental(userId, bookId);
    }

    public void updateRental(Long rentalId) {
        jdbcBookRepository.updateRental(rentalId);
    }
}