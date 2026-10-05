package example.umc_11th_web_spring.controller;

import example.umc_11th_web_spring.dto.BookResponse;
import example.umc_11th_web_spring.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /*
    // 도서 등록
    @PostMapping
    public String createBook(@RequestBody Map<String, Object> body) {
        bookService.createBook(body);
        return "도서 등록이 완료되었습니다!";
    }

    // 카테고리별 도서 목록 조회
    @GetMapping("/category/{categoryId}")
    public List<Map<String, Object>> getBooksByCategory(
            @PathVariable Long categoryId
    ) {
        return bookService.getBooksByCategory(categoryId);
    }
    */
}