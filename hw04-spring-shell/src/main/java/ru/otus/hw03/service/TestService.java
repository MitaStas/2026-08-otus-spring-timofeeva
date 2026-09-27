package ru.otus.hw03.service;

import ru.otus.hw03.domain.Question;
import ru.otus.hw03.domain.Student;
import ru.otus.hw03.domain.TestResult;

import java.util.List;

public interface TestService {
    TestResult executeTestFor(Student student);

    List<Question> getQuestions();
}
