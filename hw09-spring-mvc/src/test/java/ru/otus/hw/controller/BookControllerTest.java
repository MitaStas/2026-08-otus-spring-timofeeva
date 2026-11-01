package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldShowBooks() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(book()));
        when(commentService.findByBookId(1L)).thenReturn(List.of(new Comment(4L, "Comment", book())));
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("listBooks"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    void shouldShowFormForExistingBook() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.of(book()));
        when(authorService.findAll()).thenReturn(List.of(new Author(2L, "Author")));
        when(genreService.findAll()).thenReturn(List.of(new Genre(3L, "Genre")));
        mockMvc.perform(get("/books").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editBook"))
                .andExpect(model().attributeExists("book", "authors", "genres"));
    }

    @Test
    void shouldShowErrorPageWhenBookNotFound() throws Exception {
        when(bookService.findById(100L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/books").param("id", "100"))
                .andExpect(status().isOk())
                .andExpect(view().name("customError"))
                .andExpect(model().attribute("errorText", "Сущность не найдена"));
    }

    @Test
    void shouldCreateBook() throws Exception {
        mockMvc.perform(post("/books")
                        .param("id", "0")
                        .param("title", "NewBook")
                        .param("authorId", "2")
                        .param("genreIds", "3", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
        verify(bookService).update(eq(0L), eq("NewBook"), eq(2L), eq(Set.of(3L, 4L)));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        mockMvc.perform(post("/books")
                        .param("id", "1")
                        .param("title", "Updated")
                        .param("authorId", "2")
                        .param("genreIds", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
        verify(bookService).update(eq(1L), eq("Updated"), eq(2L), eq(Set.of(3L)));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/books").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
        verify(bookService).deleteById(1L);
    }

    @Test
    void shouldReturnEditPageWhenAuthorIdCannotBeParsed() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(new Author(2L, "Author")));
        when(genreService.findAll()).thenReturn(List.of(new Genre(3L, "Genre")));
        mockMvc.perform(post("/books")
                        .param("id", "0")
                        .param("title", "NewBook")
                        .param("authorId", "wrong-id")
                        .param("genreIds", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("editBook"))
                .andExpect(model().attributeHasErrors("book"));
    }

    @Test
    void shouldReturnEditPageWhenAuthorIsNotSelected() throws Exception {
        prepareBookFormAttributes();
        mockMvc.perform(post("/books")
                        .param("id", "0")
                        .param("title", "NewBook")
                        .param("genreIds", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("editBook"))
                .andExpect(model().attributeHasErrors("book"));
        verifyNoInteractions(bookService);
    }

    @Test
    void shouldReturnEditPageWhenGenresAreNotSelected() throws Exception {
        prepareBookFormAttributes();

        mockMvc.perform(post("/books")
                        .param("id", "0")
                        .param("title", "NewBook")
                        .param("authorId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("editBook"))
                .andExpect(model().attributeHasErrors("book"));
        verifyNoInteractions(bookService);
    }

    @Test
    void shouldReturnEditPageWhenBookServiceCannotFindRelatedEntity() throws Exception {
        prepareBookFormAttributes();
        when(bookService.update(0L, "NewBook", 2L, Set.of(3L)))
                .thenThrow(new EntityNotFoundException("Автор с таким ID не найден"));
        mockMvc.perform(post("/books")
                        .param("id", "0")
                        .param("title", "NewBook")
                        .param("authorId", "2")
                        .param("genreIds", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("editBook"))
                .andExpect(model().attributeHasErrors("book"));
    }

    private void prepareBookFormAttributes() {
        when(authorService.findAll()).thenReturn(List.of(new Author(2L, "Author")));
        when(genreService.findAll()).thenReturn(List.of(new Genre(3L, "Genre")));
    }

    private Book book() {
        var book = new Book(1L, "Book", new Author(2L, "Author"), List.of(new Genre(3L, "Genre")));
        book.setComments(Set.of(new Comment(4L, "Comment", book)));
        return book;
    }
}
