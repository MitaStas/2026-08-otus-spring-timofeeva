package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldFindBookById() {
        var book = book(1, "Book", author(1), List.of(genre(1)));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        var result = bookService.findById(1L);
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Book");
        assertThat(result.getAuthor().getFullName()).isEqualTo("Author 1");
        assertThat(result.getGenres()).extracting("name").containsExactly("Genre 1");
        verify(bookRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenBookIsNotFoundById() {
        when(bookRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.findById(42L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldFindAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(
                book(1, "First", author(1), List.of(genre(1))),
                book(2, "Second", author(2), List.of(genre(2)))));
        var result = bookService.findAll();
        assertThat(result).extracting("title").containsExactly("First", "Second");
        verify(bookRepository).findAll();
    }

    @Test
    void shouldInsertBookWithExistingAuthorAndGenres() {
        var request = new BookCreateDto("New book", 1L, List.of(2L, 3L));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author(1)));
        when(genreRepository.findAllById(anySet())).thenReturn(List.of(genre(2), genre(3)));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var result = bookService.insert(request);
        var bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        assertThat(bookCaptor.getValue().getTitle()).isEqualTo("New book");
        assertThat(bookCaptor.getValue().getAuthor()).isEqualTo(author(1));
        assertThat(bookCaptor.getValue().getGenres()).containsExactly(genre(2), genre(3));
        assertThat(result.getTitle()).isEqualTo("New book");
    }

    @Test
    void shouldRejectInsertWhenAuthorDoesNotExist() {
        when(authorRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.insert(new BookCreateDto("Book", 42L, List.of(1L))))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Author with id 42 not found");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldRejectInsertWhenAtLeastOneGenreDoesNotExist() {
        var request = new BookCreateDto("Book", 1L, List.of(2L, 3L));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author(1)));
        when(genreRepository.findAllById(anySet())).thenReturn(List.of(genre(2)));
        assertThatThrownBy(() -> bookService.insert(request)).isInstanceOf(EntityNotFoundException.class);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldUpdateExistingBook() {
        var existingBook = book(1, "Old title", author(1), List.of(genre(1)));
        var request = new BookUpdateDto(1L, "Updated title", 2L, List.of(3L));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(2L)).thenReturn(Optional.of(author(2)));
        when(genreRepository.findAllById(anySet())).thenReturn(List.of(genre(3)));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        var result = bookService.update(request);
        assertThat(result.getTitle()).isEqualTo("Updated title");
        assertThat(existingBook.getAuthor()).isEqualTo(author(2));
        assertThat(existingBook.getGenres()).containsExactly(genre(3));
        verify(bookRepository).save(existingBook);
    }

    @Test
    void shouldRejectUpdateWhenBookDoesNotExist() {
        var request = new BookUpdateDto(42L, "Title", 1L, List.of(1L));
        when(bookRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.update(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Book with id 42 not found");
    }

    @Test
    void shouldDeleteBookById() {
        bookService.deleteById(1L);
        verify(bookRepository).deleteById(1L);
    }

    private Author author(long id) {
        return new Author(id, "Author " + id);
    }

    private Genre genre(long id) {
        return new Genre(id, "Genre " + id);
    }

    private Book book(long id, String title, Author author, List<Genre> genres) {
        return new Book(id, title, author, genres);
    }
}
