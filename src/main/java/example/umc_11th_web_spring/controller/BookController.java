package example.umc_11th_web_spring.controller;

import example.umc_11th_web_spring.dto.BookReqDTO;
import example.umc_11th_web_spring.dto.BookResDTO;
import example.umc_11th_web_spring.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/books")
    public List<BookResDTO.BookResponse> getBooks() {
        return bookService.getBooks();
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookResDTO.BookResponse createBook(@RequestBody @Valid BookReqDTO.CreateBookRequest createBookReq) {
        return bookService.createBook(createBookReq);
    }

    @GetMapping("/books/category/{categoryId}")
    public List<Map<String, Object>> getBooksByCategory(@PathVariable("categoryId") Long categoryId){
        return  bookService.getBooksByCategoryId(categoryId);
    }

    @PostMapping("/rentals")
    public String createRental(@RequestBody @Valid BookReqDTO.CreateRentalDTO createRentalDTO){
        bookService.createRental(createRentalDTO.userId(), createRentalDTO.bookId());
        return "대여가 완료되었습니다!";
    }

    @PatchMapping("/rentals/{rentalId}/return")
    public String returnBook(@PathVariable("rentalId") Long rentalId){
        bookService.updateRental(rentalId);
        return "반납이 완료되었습니다!";
    }
}
