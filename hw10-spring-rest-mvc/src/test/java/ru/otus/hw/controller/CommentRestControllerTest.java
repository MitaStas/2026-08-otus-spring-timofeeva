package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.rest.CommentRestController;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldReturnCommentsByBookId() throws Exception {
        var comment1 = new CommentDto(2L, "Comment");
        var comment2 = new CommentDto(2L, "Comment");
        when(commentService.findByBookId(1L)).thenReturn(List.of(comment1, comment2));
        mockMvc.perform(get("/api/books/{bookId}/comments", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(comment1.getId()))
                .andExpect(jsonPath("$[0].text").value(comment1.getText()))
                .andExpect(jsonPath("$[1].id").value(comment2.getId()))
                .andExpect(jsonPath("$[1].text").value(comment2.getText()));
    }

    @Test
    void shouldCreateComment() throws Exception {
        var comment1 = new CommentDto(0,  "Comment");
        mockMvc.perform(post("/api/books/{bookId}/comments", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment1)))
                .andExpect(status().isCreated());
        verify(commentService).addComment(1L, comment1.getText());
    }

    @Test
    void shouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/books/{bookId}/comments/{commentId}", 1, 2))
                .andExpect(status().isNoContent());
        verify(commentService).deleteById(2L);
    }
}
