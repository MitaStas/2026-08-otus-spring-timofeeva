package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA-репозиторий книг")
@DataJpaTest
@Import(JpaBookRepository.class)
class JpaBookRepositoryTest {

    @Autowired
    private JpaBookRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Author firstAuthor;

    private Genre firstGenre;

    private Genre secondGenre;

    private Book firstBook;

    private Book secondBook;

    private Book thirdBook;

    @BeforeEach
    void setUp() {
        firstAuthor = entityManager.persist(new Author(0, "Author one"));
        var secondAuthor = entityManager.persist(new Author(0, "Author two"));
        firstGenre = entityManager.persist(new Genre(0, "Genre one"));
        secondGenre = entityManager.persist(new Genre(0, "Genre two"));
        var thirdGenre = entityManager.persist(new Genre(0, "Genre three"));
        firstBook = entityManager.persist(new Book(0, "First book", firstAuthor, List.of(firstGenre, secondGenre)));
        secondBook = entityManager.persist(new Book(0, "Second book", secondAuthor, List.of(thirdGenre)));
        thirdBook = entityManager.persist(new Book(0, "Third book", firstAuthor, List.of()));
        entityManager.persist(new Comment(0, "First comment", firstBook));
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldFindBookWithAuthorAndGenresById() {
        var actual = repository.findById(firstBook.getId()).orElseThrow();
        assertThat(actual.getTitle()).isEqualTo("First book");
        assertThat(actual.getAuthor().getId()).isEqualTo(firstAuthor.getId());
        assertThat(actual.getGenres()).extracting(Genre::getId)
                .containsExactlyInAnyOrder(firstGenre.getId(), secondGenre.getId());
    }

    @Test
    void shouldFindBookWithAuthorAndWithoutGenresById() {
        var actual = repository.findById(thirdBook.getId()).orElseThrow();
        assertThat(actual.getTitle()).isEqualTo("Third book");
        assertThat(actual.getAuthor().getId()).isEqualTo(firstAuthor.getId());
        assertThat(actual.getGenres()).isEmpty();
    }


    @Test
    void shouldFindAllBooks() {
        var books = repository.findAll();
        assertThat(books).extracting(Book::getId)
                .containsExactlyInAnyOrder(firstBook.getId(), secondBook.getId(), thirdBook.getId());
        assertThat(books).allSatisfy(book -> assertThat(book.getAuthor()).isNotNull());
        assertThat(books.get(0).getGenres()).hasSize(2);
        assertThat(books.get(1).getGenres()).hasSize(1);
        assertThat(books.get(2).getGenres()).isEmpty();
    }

    @Test
    void shouldSaveNewBook() {
        var book = new Book(0, "New book", firstAuthor, List.of(firstGenre));
        var saved = repository.save(book);
        entityManager.flush();
        entityManager.clear();
        var persisted = entityManager.find(Book.class, saved.getId());
        assertThat(persisted).isNotNull();
        assertThat(persisted.getTitle()).isEqualTo("New book");
        assertThat(persisted.getAuthor().getId()).isEqualTo(firstAuthor.getId());
        assertThat(persisted.getGenres()).extracting(Genre::getId).containsExactly(firstGenre.getId());
    }

    @Test
    void shouldUpdateBook() {
        var replacement = new Book(firstBook.getId(), "Updated book", firstAuthor, List.of(secondGenre));
        repository.save(replacement);
        entityManager.flush();
        entityManager.clear();
        var persisted = entityManager.find(Book.class, firstBook.getId());
        assertThat(persisted.getTitle()).isEqualTo("Updated book");
        assertThat(persisted.getGenres()).extracting(Genre::getId).containsExactly(secondGenre.getId());
    }

    @Test
    void shouldDeleteBook() {
        repository.deleteById(firstBook.getId());
        entityManager.flush();
        entityManager.clear();
        assertThat(entityManager.find(Book.class, firstBook.getId())).isNull();
    }
}
