package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    void shouldFindGenreById() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(new Genre(1, "Fantasy")));
        var result = genreService.findById(1L);
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Fantasy");
    }

    @Test
    void shouldThrowWhenGenreIsNotFoundById() {
        when(genreRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> genreService.findById(42L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldFindAllGenres() {
        when(genreRepository.findAll()).thenReturn(List.of(new Genre(1, "Fantasy"), new Genre(2, "Drama")));
        assertThat(genreService.findAll()).extracting("name").containsExactly("Fantasy", "Drama");
        verify(genreRepository).findAll();
    }

    @Test
    void shouldInsertGenre() {
        when(genreRepository.save(new Genre(0, "Fantasy"))).thenReturn(new Genre(1, "Fantasy"));
        var result = genreService.insert("Fantasy");
        var genreCaptor = ArgumentCaptor.forClass(Genre.class);
        verify(genreRepository).save(genreCaptor.capture());
        assertThat(genreCaptor.getValue()).usingRecursiveComparison().isEqualTo(new Genre(0, "Fantasy"));
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void shouldUpdateExistingGenre() {
        var genre = new Genre(1, "Fantasy");
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.save(genre)).thenReturn(genre);
        var result = genreService.update(1L, "Science fiction");
        assertThat(result.getName()).isEqualTo("Science fiction");
        verify(genreRepository).save(genre);
    }

    @Test
    void shouldThrowWhenUpdatingMissingGenre() {
        when(genreRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> genreService.update(42L, "Fantasy")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldDeleteGenreById() {
        genreService.deleteById(1L);
        verify(genreRepository).deleteById(1L);
    }
}
