package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Author;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private long id;

    @NotBlank(message = "Не должно быть пустым")
    private String fullName;

    public static AuthorDto fromDomainObject(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}
