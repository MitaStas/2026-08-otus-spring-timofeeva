package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TestServiceImplTest {

    @Test
    void shouldAskAllQuestionsAndReturnTestResultWithRightAnswersCount() {
        var ioService = mock(IOService.class);
        var questionDao = mock(QuestionDao.class);
        var student = new Student("Ivan", "Ivanov");
        var firstQuestion = new Question("What is Java?", List.of(
                new Answer("A programming language", true),
                new Answer("A database", false),
                new Answer("An operating system", false)));
        var secondQuestion = new Question("What is Spring?", List.of(
                new Answer("A framework", true),
                new Answer("An IDE", false)));
        var questions = List.of(firstQuestion, secondQuestion);
        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRangeWithPrompt(1, 3,
                "Choose number of answer:", "Invalid value. Try again")).thenReturn(1);
        when(ioService.readIntForRangeWithPrompt(1, 2,
                "Choose number of answer:", "Invalid value. Try again")).thenReturn(2);
        var testService = new TestServiceImpl(ioService, questionDao);

        var result = testService.executeTestFor(student);

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).containsExactly(firstQuestion, secondQuestion);
        assertThat(result.getRightAnswersCount()).isEqualTo(1);

        InOrder inOrder = inOrder(ioService, questionDao);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");
        inOrder.verify(questionDao).findAll();
        verifyQuestionInteraction(inOrder, ioService, firstQuestion);
        inOrder.verify(ioService).readIntForRangeWithPrompt(1, 3,
                "Choose number of answer:", "Invalid value. Try again");
        inOrder.verify(ioService).printLine("");
        verifyQuestionInteraction(inOrder, ioService, secondQuestion);
        inOrder.verify(ioService).readIntForRangeWithPrompt(1, 2,
                "Choose number of answer:", "Invalid value. Try again");
        inOrder.verify(ioService).printLine("");
        verifyNoMoreInteractions(ioService, questionDao);
    }

    private void verifyQuestionInteraction(InOrder inOrder, IOService ioService, Question question) {
        inOrder.verify(ioService).printLine(question.text());
        for (int answerIndex = 0; answerIndex < question.answers().size(); answerIndex++) {
            var answer = question.answers().get(answerIndex);
            inOrder.verify(ioService).printFormattedLine("%s. %s", answerIndex + 1, answer.text());
        }
    }
}
