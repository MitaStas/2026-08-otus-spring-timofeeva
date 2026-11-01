package ru.otus.hw.services;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentService {
    Optional<Comment> findById(String id);

    List<Comment> findAll();

    List<Comment> findByBookId(String bookId);

    Book addComments(String bookId, Set<String> commentsText);

    Comment update(String id, String text);

    void deleteById(String id);
}
