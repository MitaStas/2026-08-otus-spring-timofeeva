package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document(collection = "comments")
@ToString(exclude = "book")
@EqualsAndHashCode(exclude = "book")
public class Comment {

    @Id
    private String id;

    private String text;

    @DocumentReference(lazy = true)
    private Book book;

}
