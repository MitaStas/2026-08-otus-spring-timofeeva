package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.utils.FileUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvQuestionDaoTest {

    @Test
    void shouldThrowExceptionWhenResourceDoesNotExist() {
        TestFileNameProvider fileNameProvider = () -> "missing-questions.csv";
        var questionDao = new CsvQuestionDao(fileNameProvider, new FileUtil());
        assertThatThrownBy(questionDao::findAll)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Не найден ресурс с именем " + fileNameProvider.getTestFileName());
    }

    @Test
    void shouldReadQuestionsFromTestResource() {
        TestFileNameProvider fileNameProvider = () -> "questions-for-dao-test.csv";
        var questionDao = new CsvQuestionDao(fileNameProvider, new FileUtil());
        assertThat(questionDao.findAll()).containsExactly(
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
