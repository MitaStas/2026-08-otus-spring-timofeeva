package ru.otus.hw.dto;

import java.util.List;

public interface BookDto {
    String getTitle();

    Long getAuthorId();

    List<Long> getGenreIds();

    void setTitle(String title);

    void setAuthorId(Long authorId);

    void setGenreIds(List<Long> genreIds);

}
