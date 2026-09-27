package ru.otus.hw03.service;

import ru.otus.hw03.domain.Student;

public interface StudentService {

    Student determineCurrentStudent();

    Student create(String firstName, String lastName);
}
