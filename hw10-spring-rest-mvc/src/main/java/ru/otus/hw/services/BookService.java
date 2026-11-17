package ru.otus.hw.services;

import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateDto;

import java.util.List;

public interface BookService {
    BookResponseDto findById(long id);

    List<BookResponseDto> findAll();

    BookResponseDto insert(BookCreateDto book);

    BookResponseDto update(BookUpdateDto book);

    void deleteById(long id);
}
