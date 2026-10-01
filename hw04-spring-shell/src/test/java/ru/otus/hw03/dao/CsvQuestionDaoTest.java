package ru.otus.hw03.dao;

import org.junit.jupiter.api.Test;
import ru.otus.hw03.config.TestFileNameProvider;
import ru.otus.hw03.domain.Answer;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.utils.FileUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvQuestionDaoTest {

    @Test
    void shouldReadQuestionsFromCsvFile() {
        TestFileNameProvider fileNameProvider = () -> "questions-for-dao-test.csv";
        var questionDao = new CsvQuestionDao(fileNameProvider, new FileUtil());
        var questions = questionDao.findAll();
        assertThat(questions).containsExactly(
                new Question("Which Java collection keeps insertion order?", List.of(
                        new Answer("LinkedHashSet", true),
                        new Answer("HashSet", false),
                        new Answer("TreeSet", false))),
                new Question("What is the default value of a boolean field?", List.of(
                        new Answer("false", true),
                        new Answer("true", false),
                        new Answer("null", false))));
    }
}
