package example.umc_11th_web_spring.controller;

import example.umc_11th_web_spring.dto.BookReqDTO;
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

    @PostMapping("/books")
    public String createBook(@RequestBody Map<String, Object> body){
        bookService.createBook(body);
        return "도서 등록이 완료되었습니다!";
    }

    @GetMapping("/books/category/{categoryId}")
    public List<Map<String, Object>> getBooksByCategory(@PathVariable("categoryId") Long categoryId){
        return  bookService.getBooksByCategoryId(categoryId);
    }

    @PostMapping("/rentals")
    public String createRental(@RequestBody BookReqDTO.CreateRentalDTO createRentalDTO){
        bookService.createRental(createRentalDTO.userId(), createRentalDTO.bookId());
        return "대여가 완료되었습니다!";
    }

    @PatchMapping("/rentals/{rentalId}/return")
    public String returnBook(@PathVariable("rentalId") Long rentalId){
        bookService.updateRental(rentalId);
        return "반납이 완료되었습니다!";
    }
}
