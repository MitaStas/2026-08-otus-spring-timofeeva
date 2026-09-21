package ru.otus.hw03.service;

import ru.otus.hw03.domain.Student;
import ru.otus.hw03.domain.TestResult;

public interface TestService {
    TestResult executeTestFor(Student student);
}
