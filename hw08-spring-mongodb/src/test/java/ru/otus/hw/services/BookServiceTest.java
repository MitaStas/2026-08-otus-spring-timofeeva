package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookDetails;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldFindBookWithAuthorAndGenresById() {
        var author = new Author("1", "Author one");
        var genre = new Genre("1", "Genre one");
        var book = new BookDetails("1", "First book", author, List.of(genre));
        when(bookRepository.findWithDetailsById("1")).thenReturn(Optional.of(book));
        assertThat(bookService.findById("1")).contains(book);
        verify(bookRepository).findWithDetailsById("1");
    }

    @Test
    void shouldReturnEmptyWhenBookIsNotFound() {
        when(bookRepository.findWithDetailsById("missing")).thenReturn(Optional.empty());
        assertThat(bookService.findById("missing")).isEmpty();
    }

    @Test
    void shouldFindAllBooksWithDetails() {
        var firstBook = new BookDetails("1", "First book", new Author("1", "Author"), List.of());
        var secondBook = new BookDetails("2", "Second book", new Author("2", "Another author"), List.of());
        when(bookRepository.findAllWithDetails()).thenReturn(List.of(firstBook, secondBook));
        assertThat(bookService.findAll()).containsExactly(firstBook, secondBook);
        verify(bookRepository).findAllWithDetails();
    }

    @Test
    void shouldInsertBookWithAuthorAndGenres() {
        var genreIds = Set.of("1", "2");
        when(authorRepository.findById("1")).thenReturn(Optional.of(new Author("1", "Author")));
        when(genreRepository.findAllById(genreIds))
                .thenReturn(List.of(new Genre("1", "First"), new Genre("2", "Second")));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        bookService.insert("New book", "1", genreIds);
        var savedBook = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(savedBook.capture());
        assertThat(savedBook.getValue().getId()).isNull();
        assertThat(savedBook.getValue().getTitle()).isEqualTo("New book");
        assertThat(savedBook.getValue().getAuthorId()).isEqualTo("1");
        assertThat(savedBook.getValue().getGenreIds()).containsExactlyInAnyOrder("1", "2");
    }

    @Test
    void shouldUpdateExistingBook() {
        var existingBook = new Book("book-1", "Old title", "1", Set.of("1"));
        var genreIds = Set.of("2");
        when(authorRepository.findById("2")).thenReturn(Optional.of(new Author("2", "Author")));
        when(genreRepository.findAllById(genreIds)).thenReturn(List.of(new Genre("2", "Genre")));
        when(bookRepository.findById("book-1")).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookService.update("book-1", "Updated title", "2", genreIds);

        var savedBook = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(savedBook.capture());
        assertThat(savedBook.getValue().getId()).isEqualTo("book-1");
        assertThat(savedBook.getValue().getTitle()).isEqualTo("Updated title");
        assertThat(savedBook.getValue().getAuthorId()).isEqualTo("2");
        assertThat(savedBook.getValue().getGenreIds()).containsExactly("2");
    }

    @Test
    void shouldRejectEmptyGenres() {
        assertThatThrownBy(() -> bookService.insert("Book", "1", Set.of()))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(authorRepository, genreRepository, bookRepository);
    }

    @Test
    void shouldRejectMissingAuthor() {
        when(authorRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.insert("Book", "missing", Set.of("1")))
                .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(genreRepository);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldRejectMissingGenre() {
        var genreIds = Set.of("1", "missing");
        when(authorRepository.findById("1")).thenReturn(Optional.of(new Author("1", "Author")));
        when(genreRepository.findAllById(genreIds)).thenReturn(List.of(new Genre("1", "Genre")));

        assertThatThrownBy(() -> bookService.insert("Book", "1", genreIds))
                .isInstanceOf(EntityNotFoundException.class);

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldDeleteCommentsBeforeBook() {
        bookService.deleteById("book-1");

        var order = inOrder(commentRepository, bookRepository);
        order.verify(commentRepository).deleteByBookId("book-1");
        order.verify(bookRepository).deleteById("book-1");
    }
}
