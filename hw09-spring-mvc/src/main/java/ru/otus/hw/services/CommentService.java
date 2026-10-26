package ru.otus.hw.services;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentService {
    Optional<Comment> findById(long id);

    List<Comment> findAll();

    List<Comment> findByBookId(long bookId);

    Book  addComments(long bookId, Set<String> commentsText);

    Comment update(long id, String text);

    void deleteById(long id);
}
