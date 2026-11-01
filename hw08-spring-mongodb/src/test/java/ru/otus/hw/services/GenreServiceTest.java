package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    void shouldFindAllGenres() {
        var genres = List.of(new Genre("1", "First genre"), new Genre("2", "Second genre"));
        when(genreRepository.findAll()).thenReturn(genres);
        assertThat(genreService.findAll()).containsExactlyElementsOf(genres);
        verify(genreRepository).findAll();
    }
}
