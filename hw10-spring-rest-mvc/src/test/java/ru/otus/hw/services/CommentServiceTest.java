package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void shouldFindCommentById() {
        var comment = new Comment(1, "Text", book());
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        var result = commentService.findById(1L);
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getText()).isEqualTo("Text");
        verify(commentRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenCommentIsNotFoundById() {
        when(commentRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentService.findById(42L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldFindAllComments() {
        when(commentRepository.findAll()).thenReturn(List.of(
                new Comment(1, "First", book()), new Comment(2, "Second", book())));
        assertThat(commentService.findAll()).extracting("text").containsExactly("First", "Second");
        verify(commentRepository).findAll();
    }

    @Test
    void shouldFindCommentsByBookId() {
        when(commentRepository.findByBookId(1L)).thenReturn(List.of(new Comment(1, "Text", book())));
        assertThat(commentService.findByBookId(1L)).extracting("text").containsExactly("Text");
        verify(commentRepository).findByBookId(1L);
    }

    @Test
    void shouldAddCommentToExistingBook() {
        var book = book();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var result = commentService.addComment(1L, "New comment");
        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        assertThat(commentCaptor.getValue().getText()).isEqualTo("New comment");
        assertThat(commentCaptor.getValue().getBook()).isSameAs(book);
        assertThat(book.getComments()).contains(commentCaptor.getValue());
        assertThat(result.getText()).isEqualTo("New comment");
    }

    @Test
    void shouldRejectAddingCommentToMissingBook() {
        when(bookRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentService.addComment(42L, "Text"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Book with id 42 not found");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void shouldRejectEmptyComment() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book()));
        assertThatThrownBy(() -> commentService.addComment(1L, ""))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Comment can not be empty");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void shouldUpdateExistingComment() {
        var comment = new Comment(1, "Old text", book());
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        var result = commentService.update(1L, "New text");
        assertThat(result.getText()).isEqualTo("New text");
        verify(commentRepository).save(comment);
    }

    @Test
    void shouldRejectUpdateWhenCommentDoesNotExist() {
        when(commentRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentService.update(42L, "Text"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Comment with id 42 not found");
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void shouldDeleteCommentById() {
        commentService.deleteById(1L);
        verify(commentRepository).deleteById(1L);
    }

    private Book book() {
        return new Book(1, "Book", new Author(1, "Author"), List.of(new Genre(1, "Genre")));
    }
}
