// src/main/java/.../dto/BookResponse.java
package com.umc.study_week3.dto;

import com.umc.study_week3.entity.Book;

// Entity를 그대로 내보내지 않고, 클라이언트에 보여줄 값만 골라 담는 응답 DTO
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
