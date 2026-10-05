package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами")
@JdbcTest
@Import(JdbcGenreRepository.class)
class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository repositoryJdbc;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var actualGenres = repositoryJdbc.findAll();
        assertThat(actualGenres).containsExactlyElementsOf(dbGenres);
    }

    @DisplayName("должен загружать жанры по их id")
    @Test
    void shouldReturnCorrectGenresByIds() {
        var ids = Set.of(1L, 3L, 5L);
        var expectedGenres = List.of(dbGenres.get(0), dbGenres.get(2), dbGenres.get(4));
        var actualGenres = repositoryJdbc.findAllByIds(ids);
        assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    @DisplayName("должен возвращать пусто если не найден по id")
    @Test
    void shouldReturnEmptyGenresByIds() {
        var ids = Set.of(10L, 30L, 50L);
        var actualGenres = repositoryJdbc.findAllByIds(ids);
        assertThat(actualGenres).isEmpty();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }
}
