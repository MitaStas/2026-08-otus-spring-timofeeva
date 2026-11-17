package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA-репозиторий комментариев")
@DataJpaTest
class JpaCommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Book book;

    private Comment firstComment;

    private Comment secondComment;

    @BeforeEach
    void setUp() {
        var author = entityManager.persist(new Author(0, "Author"));
        var genre = entityManager.persist(new Genre(0, "Genre"));
        book = entityManager.persist(new Book(0, "Book", author, List.of(genre)));
        firstComment = entityManager.persist(new Comment(0, "First comment", book));
        secondComment = entityManager.persist(new Comment(0, "Second comment", book));
        entityManager.flush();
        entityManager.clear();
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
        var bookForComment = entityManager.find(Book.class, book.getId());
        var saved = repository.save(new Comment(0, "New comment", bookForComment));
        entityManager.flush();
        entityManager.clear();
        var persisted = entityManager.find(Comment.class, saved.getId());
        assertThat(persisted.getText()).isEqualTo("New comment");
        assertThat(persisted.getBook().getId()).isEqualTo(book.getId());
    }

    @Test
    void shouldDeleteComment() {
        repository.deleteById(firstComment.getId());
        var comment = repository.findById(firstComment.getId());
        assertThat(comment).isEmpty();
        entityManager.flush();
        entityManager.clear();
        assertThat(entityManager.find(Comment.class, firstComment.getId())).isNull();
    }
}
