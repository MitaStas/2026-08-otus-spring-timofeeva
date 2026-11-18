package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDto {

    private long id;

    private String title;

    private AuthorDto author;

    private List<GenreDto> genres = List.of();

    private List<CommentDto> comments = List.of();

    public static BookResponseDto fromDomainObject(Book book) {
        return fromDomainObject(book, List.of());
    }

    public static BookResponseDto fromDomainObject(Book book, List<Comment> comments) {
        return new BookResponseDto(book.getId(), book.getTitle(), AuthorDto.fromDomainObject(book.getAuthor()),
                book.getGenres().stream().map(GenreDto::fromDomainObject).toList(),
                comments.stream().map(CommentDto::fromDomainObject).toList());
    }
}
