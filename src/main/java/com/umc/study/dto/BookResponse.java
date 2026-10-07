package com.umc.study.dto;

import com.umc.study.entity.Book;

public record BookResponse(
        Long bookId,
        String title,
        String description,
        String categoryName,
        Boolean isAvailable
) {

    //Entity -> DTO
    public static BookResponse from(Book book){
        return new BookResponse(
                book.getBookId(),
                book.getTitle(),
                book.getDescription(),
                book.getCategory().getName(),
                book.getIsAvailable()
        );
    }
}
