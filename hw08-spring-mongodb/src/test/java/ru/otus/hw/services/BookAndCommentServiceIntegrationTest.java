package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@DisplayName("Интеграционные тесты сервисов книг и комментариев")
@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class BookAndCommentServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @MockitoSpyBean
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Fixture fixture;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        var author = authorRepository.save(new Author("Service author"));
        var genre = genreRepository.save(new Genre("Service genre"));
        var book = bookRepository.save(new Book("Service book", author.getId(), Set.of(genre.getId())));
        var comment = commentRepository.save(new Comment().setText("Service comment").setBook(book));
        fixture = new Fixture(book.getId(), comment.getId());
    }

    @Test
    void shouldReturnBookWithAuthorAndGenres() {
        var book = bookService.findById(fixture.bookId()).orElseThrow();
        assertThatCode(() -> book.getAuthor().getFullName()).doesNotThrowAnyException();
        assertThatCode(() -> book.getGenres().get(0).getName()).doesNotThrowAnyException();
        assertThat(book.getAuthor().getFullName()).isEqualTo("Service author");
        assertThat(book.getGenres()).extracting(Genre::getName).containsExactly("Service genre");
    }

    @Test
    void shouldReturnCommentWithBook() {
        var comment = commentService.findById(fixture.commentId()).orElseThrow();
        assertThatCode(() -> comment.getBook().getTitle()).doesNotThrowAnyException();
        assertThat(comment.getBook().getTitle()).isEqualTo("Service book");
    }

    @Test
    void shouldReturnCommentsWithBooks() {
        var comments = commentService.findByBookId(fixture.bookId());
        assertThatCode(() -> comments.forEach(comment -> comment.getBook().getTitle()))
                .doesNotThrowAnyException();
        assertThat(comments).extracting(comment -> comment.getBook().getTitle())
                .containsExactly("Service book");
    }

    @Test
    void shouldDeleteBookAndComments() {
        bookService.deleteById(fixture.bookId());
        assertThat(bookService.findById(fixture.bookId())).isEmpty();
        assertThat(commentService.findById(fixture.commentId())).isEmpty();
        assertThat(commentService.findByBookId(fixture.bookId())).isEmpty();
    }

    @Test
    void shouldKeepCommentWhenBookDeletionFails() {
        doThrow(new IllegalStateException("Book deletion failed"))
                .when(bookRepository).deleteById(fixture.bookId());
        assertThatThrownBy(() -> bookService.deleteById(fixture.bookId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Book deletion failed");
        assertThat(bookRepository.findById(fixture.bookId())).isPresent();
        assertThat(commentRepository.findById(fixture.commentId())).isPresent();
    }

    private record Fixture(String bookId,
                           String commentId) {
    }
}
