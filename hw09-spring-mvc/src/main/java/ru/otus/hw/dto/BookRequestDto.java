package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
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
public class BookRequestDto {

    private long id;

    @NotBlank(message = "Не должно быть пустым")
    @Size(min = 1, max = 100, message = "Длина должна быть от 1 до 100")
    private String title;

    @NotNull(message = "Выберите автора")
    @Positive(message = "Выберите корректного автора")
    private Long authorId;

    @NotEmpty(message = "Выберите хотя бы один жанр")
    private List<@NotNull(message = "Выберите корректные жанры")
            @Positive(message = "Выберите корректные жанры") Long> genreIds = List.of();

    public BookDto toBookDto() {
        return new BookDto(id, title, new AuthorDto(authorId, null),
                genreIds.stream().map(genreId -> new GenreDto(genreId, null)).toList(), List.of());
    }

    public static BookRequestDto fromBookDto(BookDto book) {
        return new BookRequestDto(book.getId(), book.getTitle(), book.getAuthor().getId(),
                book.getGenres().stream().map(GenreDto::getId).toList());
    }
}
