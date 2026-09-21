package ru.otus.hw03.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw03.dao.QuestionDao;
import ru.otus.hw03.domain.Answer;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.domain.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private LocalizedIOService ioService;

    @Mock
    private QuestionDao questionDao;

    @Test
    void shouldAskAllQuestionsUsingLocalizedPromptsAndReturnResult() {
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
        when(ioService.readIntForRangeWithPromptLocalized(1, 3,
                "TestService.choose.the.answer", "TestService.error.message.answer")).thenReturn(1);
        when(ioService.readIntForRangeWithPromptLocalized(1, 2,
                "TestService.choose.the.answer", "TestService.error.message.answer")).thenReturn(2);
        var service = new TestServiceImpl(ioService, questionDao);

        var result = service.executeTestFor(student);

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).containsExactly(firstQuestion, secondQuestion);
        assertThat(result.getRightAnswersCount()).isEqualTo(1);

        var inOrder = inOrder(ioService, questionDao);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printLineLocalized("TestService.answer.the.questions");
        inOrder.verify(ioService).printLine("");
        inOrder.verify(questionDao).findAll();
        verifyQuestionInteraction(inOrder, firstQuestion);
        inOrder.verify(ioService).readIntForRangeWithPromptLocalized(1, 3,
                "TestService.choose.the.answer", "TestService.error.message.answer");
        inOrder.verify(ioService).printLine("");
        verifyQuestionInteraction(inOrder, secondQuestion);
        inOrder.verify(ioService).readIntForRangeWithPromptLocalized(1, 2,
                "TestService.choose.the.answer", "TestService.error.message.answer");
        inOrder.verify(ioService).printLine("");
        verifyNoMoreInteractions(ioService, questionDao);
    }

    private void verifyQuestionInteraction(InOrder inOrder, Question question) {
        inOrder.verify(ioService).printLine(question.text());
        for (int answerIndex = 0; answerIndex < question.answers().size(); answerIndex++) {
            var answer = question.answers().get(answerIndex);
            inOrder.verify(ioService).printFormattedLine("%s. %s", answerIndex + 1, answer.text());
        }
    }
}
