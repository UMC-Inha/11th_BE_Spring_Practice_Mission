package com.umc.study.dto;

import com.umc.study.entity.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookDto {

    public record CreateBookRequest(
            @NotNull Long categoryId,
            @NotBlank @Size(max = 100) String title,
            String description
    ) {}

    public record BookResponse(
            Long bookId,
            String title,
            String description,
            String categoryName,
            Boolean isAvailable
    ) {
        public static BookResponse from(Book book) {
            return new BookResponse(
                    book.getBookId(),
                    book.getTitle(),
                    book.getDescription(),
                    book.getCategory().getName(),
                    book.getIsAvailable()
            );
        }
    }
}
