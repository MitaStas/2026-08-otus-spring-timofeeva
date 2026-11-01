package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookDetails;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'authors', localField: 'authorId', foreignField: '_id', as: 'author' } }",
            "{ $set: { author: { $first: '$author' } } }",
            "{ $lookup: { from: 'genres', localField: 'genreIds', foreignField: '_id', as: 'genres' } }"
    })
    List<BookDetails> findAllWithDetails();

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $lookup: { from: 'authors', localField: 'authorId', foreignField: '_id', as: 'author' } }",
            "{ $set: { author: { $first: '$author' } } }",
            "{ $lookup: { from: 'genres', localField: 'genreIds', foreignField: '_id', as: 'genres' } }"
    })
    Optional<BookDetails> findWithDetailsById(String id);
}
