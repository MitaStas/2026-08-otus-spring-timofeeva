package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TestServiceImplTest {

    @Test
    void shouldPrintIntroductionAndQuestionsWithNumberedAnswers() {
        var ioService = mock(IOService.class);
        var questionDao = mock(QuestionDao.class);
        var questions = List.of(
                new Question("What is Java?", List.of(
                        new Answer("Programming language", true),
                        new Answer("Database", false))),
                new Question("What is Spring?", List.of(new Answer("Framework", true))));
        when(questionDao.findAll()).thenReturn(questions);
        var testService = new TestServiceImpl(ioService, questionDao);

        testService.executeTest();

        InOrder inOrder = inOrder(ioService, questionDao);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");
        inOrder.verify(questionDao).findAll();
        for (Question question : questions) {
            inOrder.verify(ioService).printLine(question.text());
            inOrder.verify(ioService).printLine("Choose number of answer:");
            for (int answerIndex = 0; answerIndex < question.answers().size(); answerIndex++) {
                var answer = question.answers().get(answerIndex);
                inOrder.verify(ioService).printFormattedLine("%s. %s", answerIndex + 1, answer.text());
            }
            inOrder.verify(ioService).printLine("");
        }
        verifyNoMoreInteractions(ioService, questionDao);
    }

    @Test
    void shouldPrintOnlyIntroductionWhenThereAreNoQuestions() {
        var ioService = mock(IOService.class);
        var questionDao = mock(QuestionDao.class);
        when(questionDao.findAll()).thenReturn(List.of());
        var testService = new TestServiceImpl(ioService, questionDao);

        testService.executeTest();

        InOrder inOrder = inOrder(ioService, questionDao);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");
        inOrder.verify(questionDao).findAll();
        verifyNoMoreInteractions(ioService, questionDao);
    }
}
