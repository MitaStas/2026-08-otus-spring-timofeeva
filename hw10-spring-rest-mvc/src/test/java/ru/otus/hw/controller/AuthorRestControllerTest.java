package ru.otus.hw.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.rest.AuthorRestController;
import ru.otus.hw.models.Author;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
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

@WebMvcTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void shouldReturnAllAuthors() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(author()));
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(List.of(new AuthorDto(1L, "Author")))));
    }

    @Test
    void shouldReturnAuthorById() throws Exception {
        when(authorService.findById(1L)).thenReturn(Optional.of(author()));
        mockMvc.perform(get("/api/authors/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(new AuthorDto(1L, "Author"))));
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        when(authorService.insert(anyString())).thenReturn(author());
        mockMvc.perform(post("/api/authors")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"fullName":"Author"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().json(asJson(new AuthorDto(1L, "Author"))));
        verify(authorService).insert("Author");
    }

    @Test
    void shouldReturnValidationErrorWhenAuthorNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/authors")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"fullName":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(asJson(Map.of("fullName", "Не должно быть пустым"))));
    }

    @Test
    void shouldUpdateAuthor() throws Exception {
        when(authorService.update(1L, "New Author")).thenReturn(new Author(1L, "New Author"));
        mockMvc.perform(put("/api/authors/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"fullName":"New Author"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(new AuthorDto(1L, "New Author"))));
        verify(authorService).update(1L, "New Author");
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        mockMvc.perform(delete("/api/authors/{id}", 1))
                .andExpect(status().isNoContent());
        verify(authorService).deleteById(1L);
    }

    @Test
    void shouldReturnConflictWhenAuthorCannotBeDeleted() throws Exception {
        doThrow(new DataIntegrityViolationException("In use")).when(authorService).deleteById(1L);
        mockMvc.perform(delete("/api/authors/{id}", 1))
                .andExpect(status().isConflict())
                .andExpect(content().json(asJson(Map.of("error",
                        "Невозможно удалить сущность: она используется в одной или нескольких книгах"))));
    }

    private Author author() {
        return new Author(1L, "Author");
    }

    private String asJson(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }
}
