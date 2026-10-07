// src/main/java/.../dto/CreateBookRequest.java
package com.umc.study_week3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// POST /books 요청 본문의 약속. Controller의 @Valid가 아래 조건을 검사합니다.
public record CreateBookRequest(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 100) String title,
        String description
) {}
