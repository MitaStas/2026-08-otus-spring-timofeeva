package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookDetails;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    public String bookToString(Book book) {
        return "Id: %s, title: %s, authorId: {%s}, genreIds: [%s]".formatted(
                book.getId(),
                book.getTitle(),
                book.getAuthorId(),
                book.getGenreIds());
    }

    public String bookDetailsToString(BookDetails book) {
        return bookToString(book.getId(), book.getTitle(), book.getAuthor(), book.getGenres());
    }

    private String bookToString(String id, String title, ru.otus.hw.models.Author author,
                                java.util.List<ru.otus.hw.models.Genre> genres) {
        var genresString = genres.stream()
                .map(genreConverter::genreToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                id,
                title,
                authorConverter.authorToString(author),
                genresString
        );
    }
}
