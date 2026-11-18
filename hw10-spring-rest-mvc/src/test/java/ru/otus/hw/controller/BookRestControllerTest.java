package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.rest.BookRestController;
import ru.otus.hw.dto.*;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockitoBean
    private BookService bookService;

    @Test
    void shouldReturnAllBooks() throws Exception {
        var book = book();
        when(bookService.findAll()).thenReturn(List.of(book));
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(book.getId()))
                .andExpect(jsonPath("$[0].title").value(book.getTitle()));
    }

    @Test
    void shouldReturnBook() throws Exception {
        when(bookService.findById(1L)).thenReturn(book());
        mockMvc.perform(get("/api/books/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.author.fullName").value("Author"))
                .andExpect(jsonPath("$.genres[0].name").value("Genre"))
                .andExpect(jsonPath("$.comments").isEmpty());
    }

    @Test
    void shouldCreateBook() throws Exception {
        var book = new BookCreateDto("Book", 2L, List.of(3L));
        when(bookService.insert(any())).thenReturn(book());
        mockMvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
        verify(bookService).insert(book);
    }

    @Test
    void shouldReturnValidationErrorsWhenCreatingBook() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"title":"Book","genreIds":[3]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.authorId").value("Выберите автора"));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        var book = new BookUpdateDto(100L, "Book", 2L, List.of(3L));
        when(bookService.update(any())).thenReturn(book());
        mockMvc.perform(put("/api/books/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        book.setId(1);
        verify(bookService).update(book);
    }

    @Test
    void shouldReturnValidationErrorsWhenUpdatingBook() throws Exception {
        mockMvc.perform(put("/api/books/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"title":"Book","genreIds":[3]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.authorId").value("Выберите автора"));
    }

    @Test
    void shouldReturnValidationErrorsWhenEmptyTitle() throws Exception {
        var book = new BookCreateDto( "", 2L, List.of(3L));
        mockMvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Длина наименования должна быть от 1 до 100"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", 1))
                .andExpect(status().isNoContent());
        verify(bookService).deleteById(1L);
    }

    private BookResponseDto book() {
        return new BookResponseDto(1L, "Book", new AuthorDto(2L, "Author"),
                List.of(new GenreDto(3L, "Genre")), List.of());
    }
}
