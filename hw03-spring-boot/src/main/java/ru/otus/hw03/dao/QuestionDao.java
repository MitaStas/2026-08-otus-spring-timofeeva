package ru.otus.hw03.dao;

import ru.otus.hw03.domain.Question;

import java.util.List;

public interface QuestionDao {
    List<Question> findAll();
}
