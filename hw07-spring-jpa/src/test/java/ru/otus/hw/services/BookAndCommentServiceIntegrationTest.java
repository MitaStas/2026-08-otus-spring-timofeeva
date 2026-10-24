package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Интеграционные тесты сервисов книг и комментариев")
@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class BookAndCommentServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = transactionTemplate.execute(status -> {
            var author = new Author(0, "Service author");
            var genre = new Genre(0, "Service genre");
            entityManager.persist(author);
            entityManager.persist(genre);

            var book = new Book(0, "Service book", author, List.of(genre));
            entityManager.persist(book);

            var comment = new Comment(0, "Service comment", book);
            entityManager.persist(comment);
            entityManager.flush();
            return new Fixture(book.getId(), comment.getId());
        });
    }

    @Test
    void shouldAllowUsingBookRelationsAfterServiceTransaction() {
        var book = bookService.findById(fixture.bookId()).orElseThrow();
        assertThatCode(() -> book.getAuthor().getFullName()).doesNotThrowAnyException();
        assertThatCode(() -> book.getGenres().get(0).getName()).doesNotThrowAnyException();
        assertThat(book.getAuthor().getFullName()).isEqualTo("Service author");
        assertThat(book.getGenres()).extracting(Genre::getName).containsExactly("Service genre");
    }

    @Test
    void shouldAllowUsingCommentBookAfterServiceTransaction() {
        var comment = commentService.findById(fixture.commentId()).orElseThrow();
        assertThatCode(() -> comment.getBook().getTitle()).doesNotThrowAnyException();
        assertThat(comment.getBook().getTitle()).isEqualTo("Service book");
    }

    @Test
    void shouldAllowUsingBooksOfCommentsAfterServiceTransaction() {
        var comments = commentService.findByBookId(fixture.bookId());
        assertThatCode(() -> comments.forEach(comment -> comment.getBook().getTitle()))
                .doesNotThrowAnyException();
        assertThat(comments).extracting(comment -> comment.getBook().getTitle())
                .containsExactly("Service book");
    }

    @Test
    void shouldDeleteBookAndComment() {
        bookService.deleteById(fixture.bookId);
        var comment = commentService.findById(fixture.commentId);
        var book = bookService.findById(fixture.bookId);
        var comments = commentService.findByBookId(fixture.bookId);
        assertThat(book).isEmpty();
        assertThat(comment).isEmpty();
        assertThat(comments).isEmpty();
    }

    private record Fixture(long bookId,
                           long commentId) {
    }
}
