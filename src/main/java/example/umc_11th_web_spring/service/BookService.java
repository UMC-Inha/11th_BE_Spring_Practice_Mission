package example.umc_11th_web_spring.service;

import example.umc_11th_web_spring.dto.BookResponse;
import example.umc_11th_web_spring.dto.CreateBookRequest;
import example.umc_11th_web_spring.entity.Book;
import example.umc_11th_web_spring.entity.Category;
import example.umc_11th_web_spring.repository.BookRepository;
import example.umc_11th_web_spring.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<BookResponse> getBooks() {
        return bookRepository.findAllByOrderByBookIdDesc()
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 카테고리입니다."
                ));

        Book book = new Book(
                category,
                request.title(),
                request.description()
        );

        return BookResponse.from(bookRepository.save(book));
    }
}