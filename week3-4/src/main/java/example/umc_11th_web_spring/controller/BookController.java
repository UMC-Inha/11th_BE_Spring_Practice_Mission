package example.umc_11th_web_spring.controller;

import example.umc_11th_web_spring.dto.BookResponse;
import example.umc_11th_web_spring.dto.CreateBookRequest;
import example.umc_11th_web_spring.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 전체 도서 목록 조회
    @GetMapping
    public List<BookResponse> getBooks() {
        return bookService.getBooks();
    }

    // 신규 도서 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@Valid @RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }
}