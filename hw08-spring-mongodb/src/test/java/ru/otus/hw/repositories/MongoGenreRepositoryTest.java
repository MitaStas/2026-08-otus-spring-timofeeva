package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DisplayName("MongoDB-репозиторий жанров")
@DataMongoTest
class MongoGenreRepositoryTest {

    @Autowired
    private GenreRepository repository;

    private Genre firstGenre;

    private Genre secondGenre;

    private Genre thirdGenre;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        firstGenre = repository.save(new Genre("Genre one"));
        secondGenre = repository.save(new Genre("Genre two"));
        thirdGenre = repository.save(new Genre("Genre three"));
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
        var genres = repository.findAllById(Set.of(firstGenre.getId(), thirdGenre.getId()));
        assertThat(genres)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(firstGenre.getId(), thirdGenre.getId());
    }

    @Test
    void shouldReturnEmptyListForUnknownIds() {
        assertThat(repository.findAllById(Set.of("unknown"))).isEmpty();
    }
}
