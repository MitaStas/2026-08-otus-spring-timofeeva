package ru.otus.hw.services;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BookResponseDto findById(long id) {
        return bookRepository.findById(id)
                .map(BookResponseDto::fromDomainObject)
                .orElseThrow(NotFoundException::new);

    }

    @Override
    @Transactional
    public List<BookResponseDto> findAll() {
        return bookRepository.findAll().stream().map(BookResponseDto::fromDomainObject).toList();
    }

    @Override
    @Transactional
    public BookResponseDto insert(BookCreateDto bookDto) {
        var book = new Book();
        return save(bookDto, book);
    }

    @Override
    @Transactional
    public BookResponseDto update(BookUpdateDto bookUpdateDto) {
        var book = bookRepository
                .findById(bookUpdateDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found"
                        .formatted(bookUpdateDto.getId()))
                );
        return save(bookUpdateDto, book);
    }

    @Nonnull
    private BookResponseDto save(BookDto bookDto, Book book) {
        long authorId = bookDto.getAuthorId();
        Set<Long> genresIds = new HashSet<>(bookDto.getGenreIds());
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        book.setTitle(bookDto.getTitle());
        book.setAuthor(author);
        book.setGenres(genres);
        return BookResponseDto.fromDomainObject(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }
}
