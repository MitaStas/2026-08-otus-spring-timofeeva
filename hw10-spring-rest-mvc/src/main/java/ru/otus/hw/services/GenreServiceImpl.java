package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public GenreDto findById(long id) {
        return genreRepository.findById(id)
                .map(GenreDto::fromDomainObject)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll()
                .stream()
                .map(GenreDto::fromDomainObject).toList();
    }

    @Override
    public GenreDto insert(String name) {
        return GenreDto.fromDomainObject(genreRepository.save(new Genre(0, name)));
    }

    @Override
    public GenreDto update(long id, String name) {
        Genre genre = genreRepository.findById(id).orElseThrow(NotFoundException::new);
        genre.setName(name);
        return GenreDto.fromDomainObject(genreRepository.save(genre));
    }

    @Override
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }
}
