package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;

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

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void shouldShowAuthors() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(author()));
        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("listAuthors"))
                .andExpect(model().attributeExists("authors"));
    }

    @Test
    void shouldShowFormForExistingAuthor() throws Exception {
        when(authorService.findById(1L)).thenReturn(Optional.of(author()));
        mockMvc.perform(get("/authors/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editAuthor.html"))
                .andExpect(model().attributeExists("author"));
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        mockMvc.perform(post("/authors").param("fullName", "New author"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/authors"));
        verify(authorService).insert("New author");
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        mockMvc.perform(delete("/authors/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/authors"));
        verify(authorService).deleteById(1L);
    }

    private Author author() {
        return new Author(1L, "Author");
    }
}
