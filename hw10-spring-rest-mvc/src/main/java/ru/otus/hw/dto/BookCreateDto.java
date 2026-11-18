package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateDto implements BookDto {

    @Size(min = 1, max = 100, message = "Длина наименования должна быть от 1 до 100")
    private String title;

    @NotNull(message = "Выберите автора")
    @Positive(message = "Выберите корректного автора")
    private Long authorId;

    @NotEmpty(message = "Выберите хотя бы один жанр")
    private List<Long> genreIds = List.of();

}
