package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.SequenceService;

@RequiredArgsConstructor
@Configuration
public class MongoIdGenerationConfig {

    private final SequenceService sequenceService;

    @Bean
    public BeforeConvertCallback<Author> authorIdGenerator() {
        return (author, collection) -> {
            if (author.getId() == null) {
                author.setId(sequenceService.nextId("authors"));
            }
            return author;
        };
    }

    @Bean
    public BeforeConvertCallback<Genre> genreIdGenerator() {
        return (genre, collection) -> {
            if (genre.getId() == null) {
                genre.setId(sequenceService.nextId("genres"));
            }
            return genre;
        };
    }

    @Bean
    public BeforeConvertCallback<Book> bookIdGenerator() {
        return (book, collection) -> {
            if (book.getId() == null) {
                book.setId(sequenceService.nextId("books"));
            }
            return book;
        };
    }

    @Bean
    public BeforeConvertCallback<Comment> commentIdGenerator() {
        return (comment, collection) -> {
            if (comment.getId() == null) {
                comment.setId(sequenceService.nextId("comments"));
            }
            return comment;
        };
    }
}
