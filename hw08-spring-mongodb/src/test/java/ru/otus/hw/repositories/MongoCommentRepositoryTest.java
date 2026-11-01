package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.config.MongoIdGenerationConfig;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.SequenceService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MongoDB-репозиторий комментариев")
@DataMongoTest
@Import({MongoIdGenerationConfig.class, SequenceService.class})
class MongoCommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Book book;

    private Comment firstComment;

    private Comment secondComment;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        var author = authorRepository.save(new Author("Author"));
        var genre = genreRepository.save(new Genre("Genre"));
        book = bookRepository.save(new Book("Book", author.getId(), Set.of(genre.getId())));
        firstComment = repository.save(new Comment().setText("First comment").setBook(book));
        secondComment = repository.save(new Comment().setText("Second comment").setBook(book));
    }

    @Test
    void shouldFindCommentByIdWithBook() {
        var comment = repository.findById(firstComment.getId()).orElseThrow();
        assertThat(comment.getText()).isEqualTo("First comment");
        assertThat(comment.getBook().getId()).isEqualTo(book.getId());
    }

    @Test
    void shouldFindCommentsByBookId() {
        var comments = repository.findByBookId(book.getId());
        assertThat(comments).extracting(Comment::getId)
                .containsExactlyInAnyOrder(firstComment.getId(), secondComment.getId());
        assertThat(comments).allSatisfy(comment -> assertThat(comment.getBook().getId()).isEqualTo(book.getId()));
    }

    @Test
    void shouldSaveNewComment() {
        var saved = repository.save(new Comment().setText("New comment").setBook(book));
        var persisted = repository.findById(saved.getId()).orElseThrow();
        assertThat(persisted.getText()).isEqualTo("New comment");
        assertThat(persisted.getBook().getId()).isEqualTo(book.getId());
    }

    @Test
    void shouldDeleteComment() {
        repository.deleteById(firstComment.getId());
        assertThat(repository.findById(firstComment.getId())).isEmpty();
    }
}
