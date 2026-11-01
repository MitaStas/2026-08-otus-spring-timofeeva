package ru.otus.hw.repositories;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MongoDB-репозиторий книг")
@DataMongoTest
class MongoBookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Author firstAuthor;

    private Genre firstGenre;

    private Genre secondGenre;

    private Book firstBook;

    private Book secondBook;

    private Book thirdBook;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        firstAuthor = authorRepository.save(new Author("Author one"));
        var secondAuthor = authorRepository.save(new Author("Author two"));
        firstGenre = genreRepository.save(new Genre("Genre one"));
        secondGenre = genreRepository.save(new Genre("Genre two"));
        var thirdGenre = genreRepository.save(new Genre("Genre three"));
        firstBook = repository.save(new Book("First book", firstAuthor.getId(), Set.of(firstGenre.getId(), secondGenre.getId())));
        secondBook = repository.save(new Book("Second book", secondAuthor.getId(), Set.of(thirdGenre.getId())));
        thirdBook = repository.save(new Book("Third book", firstAuthor.getId(), Set.of()));
    }

    @Test
    void shouldFindBookWithAuthorAndGenresById() {
        var actual = repository.findById(firstBook.getId()).orElseThrow();
        assertThat(actual.getTitle()).isEqualTo("First book");
        assertThat(actual.getAuthorId()).isEqualTo(firstAuthor.getId());
        assertThat(actual.getGenreIds())
                .containsExactlyInAnyOrder(firstGenre.getId(), secondGenre.getId());
    }

    @Test
    void shouldFindBookWithAuthorAndWithoutGenresById() {
        var actual = repository.findById(thirdBook.getId()).orElseThrow();
        assertThat(actual.getTitle()).isEqualTo("Third book");
        assertThat(actual.getAuthorId()).isEqualTo(firstAuthor.getId());
        assertThat(actual.getGenreIds()).isEmpty();
    }

    @Test
    void shouldFindAllBooks() {
        var books = repository.findAll();
        assertThat(books).extracting(Book::getId)
                .containsExactlyInAnyOrder(firstBook.getId(), secondBook.getId(), thirdBook.getId());
        assertThat(books).allSatisfy(book -> assertThat(book.getAuthorId()).isNotNull());
        assertThat(books).filteredOn(book -> book.getId().equals(firstBook.getId()))
                .singleElement().extracting(Book::getGenreIds).asInstanceOf(InstanceOfAssertFactories.SET).hasSize(2);
        assertThat(books).filteredOn(book -> book.getId().equals(secondBook.getId()))
                .singleElement().extracting(Book::getGenreIds).asInstanceOf(InstanceOfAssertFactories.SET).hasSize(1);
        assertThat(books).filteredOn(book -> book.getId().equals(thirdBook.getId()))
                .singleElement().extracting(Book::getGenreIds).asInstanceOf(InstanceOfAssertFactories.SET).isEmpty();
    }

    @Test
    void shouldSaveNewBook() {
        var saved = repository.save(new Book("New book", firstAuthor.getId(), Set.of(firstGenre.getId())));
        var persisted = repository.findById(saved.getId()).orElseThrow();
        assertThat(persisted.getTitle()).isEqualTo("New book");
        assertThat(persisted.getAuthorId()).isEqualTo(firstAuthor.getId());
        assertThat(persisted.getGenreIds()).containsExactly(firstGenre.getId());
    }

    @Test
    void shouldCreateNotExistingBook() {
        var saved = repository.save(new Book("New book", firstAuthor.getId(), Set.of(firstGenre.getId())));
        var persisted = repository.findById(saved.getId()).orElseThrow();
        assertThat(persisted.getTitle()).isEqualTo("New book");
        assertThat(persisted.getAuthorId()).isEqualTo(firstAuthor.getId());
        assertThat(persisted.getGenreIds()).containsExactly(firstGenre.getId());
    }


    @Test
    void shouldUpdateBook() {
        firstBook.setTitle("Updated book");
        firstBook.setGenreIds(Set.of(secondGenre.getId()));
        repository.save(firstBook);
        var persisted = repository.findById(firstBook.getId()).orElseThrow();
        assertThat(persisted.getTitle()).isEqualTo("Updated book");
        assertThat(persisted.getGenreIds()).containsExactly(secondGenre.getId());
    }

    @Test
    void shouldDeleteBook() {
        repository.deleteById(firstBook.getId());
        assertThat(repository.findById(firstBook.getId())).isEmpty();
    }
}
