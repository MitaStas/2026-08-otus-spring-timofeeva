package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Comment;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private long id;

    @NotBlank(message = "Не должно быть пустым")
    private String text;

    public static CommentDto fromDomainObject(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText());
    }
}
