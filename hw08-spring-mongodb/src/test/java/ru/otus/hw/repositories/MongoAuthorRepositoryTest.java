package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@DisplayName("MongoDB-репозиторий авторов")
@DataMongoTest
class MongoAuthorRepositoryTest {

    @Autowired
    private AuthorRepository repository;

    private Author firstAuthor;

    private Author secondAuthor;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        firstAuthor = repository.save(new Author("Author one"));
        secondAuthor = repository.save(new Author("Author two"));
    }

    @Test
    void shouldFindAllAuthors() {
        assertThat(repository.findAll())
                .extracting(Author::getId, Author::getFullName)
                .containsExactlyInAnyOrder(
                        tuple(firstAuthor.getId(), firstAuthor.getFullName()),
                        tuple(secondAuthor.getId(), secondAuthor.getFullName())
                );
    }

    @Test
    void shouldFindAuthorById() {
        var author = repository.findById(firstAuthor.getId()).orElseThrow();
        assertThat(author.getFullName()).isEqualTo(firstAuthor.getFullName());
    }

    @Test
    void shouldReturnEmptyOptionalForUnknownId() {
        assertThat(repository.findById("unknown")).isEmpty();
    }
}
