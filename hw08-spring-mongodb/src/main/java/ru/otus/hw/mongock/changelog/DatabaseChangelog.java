package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Sequence;

import java.util.Set;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "anastasia", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertInitialData", author = "anastasia")
    public void insertInitialData(MongockTemplate mongoTemplate) {
        var author1 = mongoTemplate.save(author("1", "Author_1"));
        var author2 = mongoTemplate.save(author("2", "Author_2"));
        var author3 = mongoTemplate.save(author("3", "Author_3"));
        var genre1 = mongoTemplate.save(genre("1", "Genre_1"));
        var genre2 = mongoTemplate.save(genre("2", "Genre_2"));
        var genre3 = mongoTemplate.save(genre("3", "Genre_3"));
        var genre4 = mongoTemplate.save(genre("4", "Genre_4"));
        var genre5 = mongoTemplate.save(genre("5", "Genre_5"));
        var genre6 = mongoTemplate.save(genre("6", "Genre_6"));
        var book1 = mongoTemplate.save(new Book("1", "BookTitle_1", author1.getId(),
                Set.of(genre1.getId(), genre2.getId())));
        mongoTemplate.save(new Book("2", "BookTitle_2", author2.getId(), Set.of(genre3.getId(), genre4.getId())));
        mongoTemplate.save(new Book("3", "BookTitle_3", author3.getId(), Set.of(genre5.getId(), genre6.getId())));
        mongoTemplate.save(new Comment("1", "Comment_1", book1));
        mongoTemplate.save(new Comment("2", "Comment_2", book1));
        mongoTemplate.save(new Comment("3", "Comment_3", book1));
        mongoTemplate.save(new Sequence("authors", 3));
        mongoTemplate.save(new Sequence("genres", 6));
        mongoTemplate.save(new Sequence("books", 3));
        mongoTemplate.save(new Sequence("comments", 3));
    }

    private Author author(String id, String fullName) {
        var author = new Author(fullName);
        author.setId(id);
        return author;
    }

    private Genre genre(String id, String name) {
        var genre = new Genre(name);
        genre.setId(id);
        return genre;
    }
}
