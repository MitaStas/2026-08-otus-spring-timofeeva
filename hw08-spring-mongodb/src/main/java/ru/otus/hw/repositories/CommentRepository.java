package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    @Query("{ 'book' : ?0 }")
    List<Comment> findByBookId(String bookId);

    @Query(value = "{ 'book' : ?0 }", delete = true)
    void deleteByBookId(String bookId);
}
