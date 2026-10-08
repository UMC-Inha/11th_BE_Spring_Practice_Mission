package example.umc_11th_web_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 100) String title,
        String description
) {
}
