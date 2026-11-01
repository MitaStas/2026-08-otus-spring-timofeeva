package ru.otus.hw.services;

import ru.otus.hw.models.BookDetails;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookDetails> findById(String id);

    List<BookDetails> findAll();

    BookDetails insert(String title, String authorId, Set<String> genresIds);

    BookDetails update(String id, String title, String authorId, Set<String> genresIds);

    void deleteById(String id);
}
