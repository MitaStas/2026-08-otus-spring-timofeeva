package ru.otus.hw.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.rest.GenreRestController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreRestController.class)
class GenreRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GenreService genreService;

    @Test
    void shouldReturnAllGenres() throws Exception {
        when(genreService.findAll()).thenReturn(List.of(genre()));
        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(List.of(genre()))));
    }

    @Test
    void shouldReturnGenreById() throws Exception {
        when(genreService.findById(1L)).thenReturn(genre());
        mockMvc.perform(get("/api/genres/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(genre())));
    }

    @Test
    void shouldCreateGenre() throws Exception {
        when(genreService.insert("Genre")).thenReturn(genre());
        mockMvc.perform(post("/api/genres")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":"Genre"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().json(asJson(genre())));
        verify(genreService).insert("Genre");
    }

    @Test
    void shouldReturnValidationErrorWhenGenreNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/genres")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(asJson(Map.of("name", "Не должно быть пустым"))));
    }

    @Test
    void shouldUpdateGenre() throws Exception {
        when(genreService.update(1L, "New Genre")).thenReturn(new GenreDto(1L, "New Genre"));
        mockMvc.perform(put("/api/genres/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":"New Genre"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(new GenreDto(1L, "New Genre"))));
        verify(genreService).update(1L, "New Genre");
    }

    @Test
    void shouldDeleteGenre() throws Exception {
        mockMvc.perform(delete("/api/genres/{id}", 1))
                .andExpect(status().isNoContent());

        verify(genreService).deleteById(1L);
    }

    @Test
    void shouldReturnConflictWhenGenreCannotBeDeleted() throws Exception {
        doThrow(new DataIntegrityViolationException("In use")).when(genreService).deleteById(1L);
        mockMvc.perform(delete("/api/genres/{id}", 1))
                .andExpect(status().isConflict())
                .andExpect(content().json(asJson(Map.of("error",
                        "Невозможно удалить сущность: она используется в одной или нескольких книгах"))));
    }

    private GenreDto genre() {
        return new GenreDto(1L, "Genre");
    }

    private String asJson(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }
}
