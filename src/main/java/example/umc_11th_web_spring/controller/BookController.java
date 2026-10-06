package example.umc_11th_web_spring.controller;

import example.umc_11th_web_spring.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/books")
    public List<Map<String, Object>> getBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/category/{categoryId}")
    public List<Map<String, Object>> getBooksByCategoryId(
            @PathVariable Long categoryId
    ) {
        return bookService.getAllBooksByCategoryId(categoryId);
    }

    @PostMapping("/books")
    public String createBook(
            @RequestBody
            Map<String, Object> body
    ) {
        bookService.createBook(body);
        return "도서 등록이 완료되었습니다!";
    }

    @PostMapping("/rentals")
    public String createRental(
            @RequestBody
            Map<String, Object> body
    ) {
        bookService.createRental(body);
        return "도서 대여가 완료되었습니다.";
    }

    @PatchMapping("/rentals/{rentalId}/return")
    public String updateRental(
            @PathVariable Long rentalId
    ) {
        bookService.updateRental(rentalId);
        return "도서 반납이 완료되었습니다.";
    }
}
