package ru.otus.hw03.service;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw03.config.AppProperties;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.domain.Student;
import ru.otus.hw03.domain.TestResult;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
class ResultServiceImplTest {

    @MockitoBean
    private AppProperties testConfig;

    @MockitoBean
    private LocalizedIOService ioService;

    @Test
    void shouldPrintLocalizedPassingResultWhenScoreMeetsThreshold() {
        when(testConfig.getRightAnswersCountToPass()).thenReturn(2);
        var result = testResult(3, 2);
        var service = new ResultServiceImpl(testConfig, ioService);

        service.showResult(result);

        var inOrder = inOrder(testConfig, ioService);
        verifyCommonResultOutput(inOrder, result);
        inOrder.verify(testConfig).getRightAnswersCountToPass();
        inOrder.verify(ioService).printLineLocalized("ResultService.passed.test");
        verifyNoMoreInteractions(testConfig, ioService);
    }

    @Test
    void shouldPrintLocalizedFailureResultWhenScoreIsBelowThreshold() {
        when(testConfig.getRightAnswersCountToPass()).thenReturn(3);
        var result = testResult(2, 2);
        var service = new ResultServiceImpl(testConfig, ioService);

        service.showResult(result);

        var inOrder = inOrder(testConfig, ioService);
        verifyCommonResultOutput(inOrder, result);
        inOrder.verify(testConfig).getRightAnswersCountToPass();
        inOrder.verify(ioService).printLineLocalized("ResultService.fail.test");
        verifyNoMoreInteractions(testConfig, ioService);
    }

    private TestResult testResult(int answersCount, int rightAnswersCount) {
        var result = new TestResult(new Student("Ivan", "Ivanov"));
        for (int questionNumber = 1; questionNumber <= answersCount; questionNumber++) {
            result.applyAnswer(new Question("Question " + questionNumber, List.of()),
                    questionNumber <= rightAnswersCount);
        }
        return result;
    }

    private void verifyCommonResultOutput(InOrder inOrder, TestResult result) {
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printLineLocalized("ResultService.test.results");
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.student", "Ivan Ivanov");
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.answered.questions.count",
                result.getAnsweredQuestions().size());
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.right.answers.count",
                result.getRightAnswersCount());
    }
}
