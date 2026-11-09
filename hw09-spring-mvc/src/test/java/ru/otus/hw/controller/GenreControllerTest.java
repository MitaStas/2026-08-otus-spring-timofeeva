package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    void shouldShowGenres() throws Exception {
        when(genreService.findAll()).thenReturn(List.of(genre()));
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("listGenres"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    void shouldShowFormForExistingGenre() throws Exception {
        when(genreService.findById(1L)).thenReturn(Optional.of(genre()));
        mockMvc.perform(get("/genres/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editGenre"))
                .andExpect(model().attributeExists("genre"));
    }

    @Test
    void shouldCreateGenre() throws Exception {
        mockMvc.perform(post("/genres").param("name", "Novel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/genres"));
        verify(genreService).insert("Novel");
    }

    @Test
    void shouldDeleteGenre() throws Exception {
        mockMvc.perform(delete("/genres/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/genres"));
        verify(genreService).deleteById(1L);
    }

    private Genre genre() {
        return new Genre(1L, "Genre");
    }
}
