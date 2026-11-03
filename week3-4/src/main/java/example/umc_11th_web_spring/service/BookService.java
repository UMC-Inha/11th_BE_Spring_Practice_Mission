package example.umc_11th_web_spring.service;

import example.umc_11th_web_spring.dto.BookResponse;
import example.umc_11th_web_spring.dto.CreateBookRequest;
import example.umc_11th_web_spring.entity.Book;
import example.umc_11th_web_spring.entity.Category;
import example.umc_11th_web_spring.repository.BookRepository;
import example.umc_11th_web_spring.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    // 도서를 최신 등록순으로 조회하고 응답 DTO로 변환
    @Transactional(readOnly = true)
    public List<BookResponse> getBooks() {
        return bookRepository.findAllByOrderByBookIdDesc().stream()
                .map(BookResponse::from)
                .toList();
    }

    // 카테고리를 확인한 후 신규 도서를 저장하고 응답 DTO로 변환
    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        Book book = new Book(
                category,
                request.title(),
                request.description()
        );

        return BookResponse.from(bookRepository.save(book));
    }
}