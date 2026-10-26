package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA-репозиторий авторов")
@DataJpaTest
class JpaAuthorRepositoryTest {

    @Autowired
    private AuthorRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Author firstAuthor;

    private Author secondAuthor;

    @BeforeEach
    void setUp() {
        firstAuthor = entityManager.persistAndFlush(new Author(0, "Author one"));
        secondAuthor = entityManager.persistAndFlush(new Author(0, "Author two"));
        entityManager.clear();
    }

    @Test
    void shouldFindAllAuthors() {
        assertThat(repository.findAll())
                .extracting(Author::getId, Author::getFullName)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple(firstAuthor.getId(), firstAuthor.getFullName()),
                        org.assertj.core.groups.Tuple.tuple(secondAuthor.getId(), secondAuthor.getFullName())
                );
    }

    @Test
    void shouldFindAuthorById() {
        var author = repository.findById(firstAuthor.getId()).orElseThrow();
        assertThat(author.getFullName()).isEqualTo(firstAuthor.getFullName());
    }

    @Test
    void shouldReturnEmptyOptionalForUnknownId() {
        assertThat(repository.findById(100L)).isEmpty();
    }
}
