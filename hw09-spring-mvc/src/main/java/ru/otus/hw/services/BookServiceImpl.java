package ru.otus.hw.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BookDto findById(long id) {
        return bookRepository.findById(id)
                .map(BookDto::fromDomainObject)
                .orElseThrow(NotFoundException::new);

    }

    @Override
    @Transactional
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(BookDto::fromDomainObject).toList();
    }

    @Override
    @Transactional
    public BookDto insert(BookDto book) {
        return save(book, 0);
    }

    @Override
    @Transactional
    public BookDto update(BookDto book) {
        return save(book, book.getId());
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }


    private BookDto save(BookDto bookDto, long id) {
        if (bookDto.getAuthor() == null) {
            throw new IllegalArgumentException("Author must not be null");
        }
        long authorId = bookDto.getAuthor().getId();
        Set<Long> genresIds = extractGenreIds(bookDto);
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        var book = id == 0
                ? new Book()
                : bookRepository
                    .findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        book.setTitle(bookDto.getTitle());
        book.setAuthor(author);
        book.setGenres(genres);
        return BookDto.fromDomainObject(bookRepository.save(book));
    }

    private Set<Long> extractGenreIds(BookDto bookDto) {
        if (isEmpty(bookDto.getGenres())) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        return bookDto.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet());
    }

}
