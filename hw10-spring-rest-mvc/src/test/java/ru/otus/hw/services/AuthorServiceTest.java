package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void shouldFindAuthorById() {
        var author = new Author(1, "Author one");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        assertThat(authorService.findById(1L)).containsSame(author);
        verify(authorRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenAuthorIsNotFound() {
        when(authorRepository.findById(42L)).thenReturn(Optional.empty());

        assertThat(authorService.findById(42L)).isEmpty();
    }

    @Test
    void shouldFindAllAuthors() {
        when(authorRepository.findAll()).thenReturn(List.of(new Author(1, "First"), new Author(2, "Second")));
        assertThat(authorService.findAll()).extracting(Author::getFullName).containsExactly("First", "Second");
        verify(authorRepository).findAll();
    }

    @Test
    void shouldInsertAuthor() {
        when(authorRepository.save(new Author(0, "Author one"))).thenReturn(new Author(1, "Author one"));
        var result = authorService.insert("Author one");
        var authorCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorRepository).save(authorCaptor.capture());
        assertThat(authorCaptor.getValue()).usingRecursiveComparison().isEqualTo(new Author(0, "Author one"));
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        var author = new Author(1, "Old name");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);
        var result = authorService.update(1L, "New name");
        assertThat(result.getFullName()).isEqualTo("New name");
        verify(authorRepository).save(author);
    }

    @Test
    void shouldThrowWhenUpdatingMissingAuthor() {
        when(authorRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authorService.update(42L, "Name")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldDeleteAuthorById() {
        authorService.deleteById(1L);
        verify(authorRepository).deleteById(1L);
    }
}
