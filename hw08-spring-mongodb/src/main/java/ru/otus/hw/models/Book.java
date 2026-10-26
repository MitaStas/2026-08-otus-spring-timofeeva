package ru.otus.hw.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@NoArgsConstructor
@Document(collection = "books")
public class Book {

    @Id
    private String id;

    private String title;

    private String authorId;

    private Set<String> genreIds;

    public Book(String id, String title, String authorId, Set<String> genreIds) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.genreIds = genreIds;
    }

    public Book(String title, String authorId, Set<String> genreIds) {
        this.title = title;
        this.authorId = authorId;
        this.genreIds = genreIds;
    }


}
