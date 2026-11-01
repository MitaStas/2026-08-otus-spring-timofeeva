package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
        var comment = new Comment().setId("1").setText("Comment");
        when(commentRepository.findById("1")).thenReturn(Optional.of(comment));
        assertThat(commentService.findById("1")).contains(comment);
        verify(commentRepository).findById("1");
    }

    @Test
    void shouldFindAllComments() {
        var comments = List.of(new Comment().setId("1"), new Comment().setId("2"));
        when(commentRepository.findAll()).thenReturn(comments);
        assertThat(commentService.findAll()).containsExactlyElementsOf(comments);
        verify(commentRepository).findAll();
    }

    @Test
    void shouldFindCommentsByBookId() {
        var comments = List.of(new Comment().setId("1"), new Comment().setId("2"));
        when(commentRepository.findByBookId("book-1")).thenReturn(comments);
        assertThat(commentService.findByBookId("book-1")).containsExactlyElementsOf(comments);
        verify(commentRepository).findByBookId("book-1");
    }

    @Test
    void shouldAddCommentsToExistingBook() {
        var book = new Book("book-1", "Book", "author-1", Set.of("genre-1"));
        when(bookRepository.findById("book-1")).thenReturn(Optional.of(book));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        assertThat(commentService.addComments("book-1", Set.of("First", "Second"))).isSameAs(book);
        var comments = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository, times(2)).save(comments.capture());
        assertThat(comments.getAllValues())
                .extracting(Comment::getText)
                .containsExactlyInAnyOrder("First", "Second");
        assertThat(comments.getAllValues()).allSatisfy(comment -> assertThat(comment.getBook()).isSameAs(book));
    }

    @Test
    void shouldRejectCommentsForMissingBook() {
        when(bookRepository.findById("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentService.addComments("missing", Set.of("Comment")))
                .isInstanceOf(EntityNotFoundException.class);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void shouldRejectEmptyComments() {
        var book = new Book("book-1", "Book", "author-1", Set.of("genre-1"));
        when(bookRepository.findById("book-1")).thenReturn(Optional.of(book));
        assertThatThrownBy(() -> commentService.addComments("book-1", Set.of()))
                .isInstanceOf(EntityNotFoundException.class);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void shouldUpdateExistingComment() {
        var comment = new Comment().setId("1").setText("Old text");
        when(commentRepository.findById("1")).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        assertThat(commentService.update("1", "New text")).isSameAs(comment);
        assertThat(comment.getText()).isEqualTo("New text");
        verify(commentRepository).save(comment);
    }

    @Test
    void shouldRejectUpdateForMissingComment() {
        when(commentRepository.findById("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentService.update("missing", "Text"))
                .isInstanceOf(EntityNotFoundException.class);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void shouldDeleteCommentById() {
        commentService.deleteById("1");
        verify(commentRepository).deleteById("1");
    }
}
