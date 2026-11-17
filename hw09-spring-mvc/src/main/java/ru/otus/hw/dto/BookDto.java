package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {

    private long id;

    @NotBlank(message = "Не должно быть пустым")
    @Size(min = 1, max = 100, message = "Длина должна быть от 1 до 100")
    private String title;

    private AuthorDto author;

    private List<GenreDto> genres = List.of();

    private List<CommentDto> comments = List.of();

    public String genreAsString() {
        if (isEmpty(genres)) {
            return "";
        }
        return genres.stream().map(GenreDto::getName).collect(Collectors.joining(", "));
    }

    public static BookDto fromDomainObject(Book book) {
        return fromDomainObject(book, List.of());
    }

    public static BookDto fromDomainObject(Book book, List<Comment> comments) {
        return new BookDto(book.getId(), book.getTitle(), AuthorDto.fromDomainObject(book.getAuthor()),
                book.getGenres().stream().map(GenreDto::fromDomainObject).toList(),
                comments.stream().map(CommentDto::fromDomainObject).toList());
    }
}
