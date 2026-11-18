package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;

import java.util.List;

public interface GenreService {
    GenreDto findById(long id);

    List<GenreDto> findAll();

    GenreDto insert(String name);

    GenreDto update(long id, String name);

    void deleteById(long id);
}
