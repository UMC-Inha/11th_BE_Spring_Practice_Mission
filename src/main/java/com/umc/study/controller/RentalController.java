package com.umc.study.controller;

import com.umc.study.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RentalController {

    private final BookService bookService;

    @PostMapping("/rentals")
    public String createRental(@RequestBody Map<String, Object> body) {
        bookService.createRental(body);
        return "도서 대여가 완료되었습니다!";
    }
}
