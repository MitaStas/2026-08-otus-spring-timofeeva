package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DisplayName("JPA-репозиторий жанров")
@DataJpaTest
@Import(JpaGenreRepository.class)
class JpaGenreRepositoryTest {

    @Autowired
    private JpaGenreRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Genre firstGenre;

    private Genre secondGenre;

    private Genre thirdGenre;

    @BeforeEach
    void setUp() {
        firstGenre = entityManager.persistAndFlush(new Genre(0, "Genre one"));
        secondGenre = entityManager.persistAndFlush(new Genre(0, "Genre two"));
        thirdGenre = entityManager.persistAndFlush(new Genre(0, "Genre three"));
        entityManager.clear();
    }

    @Test
    void shouldFindAllGenres() {
        var genres = repository.findAll();
        assertThat(genres)
                .extracting(Genre::getId, Genre::getName)
                .containsExactlyInAnyOrder(
                        tuple(firstGenre.getId(), firstGenre.getName()),
                        tuple(secondGenre.getId(), secondGenre.getName()),
                        tuple(thirdGenre.getId(), thirdGenre.getName())
                );
    }

    @Test
    void shouldFindGenresByIds() {
        var genres = repository.findAllByIds(Set.of(firstGenre.getId(), thirdGenre.getId()));
        assertThat(genres)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(firstGenre.getId(), thirdGenre.getId());
    }

    @Test
    void shouldReturnEmptyListForUnknownIds() {
        assertThat(repository.findAllByIds(Set.of(100L))).isEmpty();
    }
}
