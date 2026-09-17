package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ResultServiceImplTest {

    @Test
    void shouldPrintPassedMessageWhenRightAnswersCountEqualsPassingScore() {
        var testConfig = mock(TestConfig.class);
        var ioService = mock(IOService.class);
        when(testConfig.getRightAnswersCountToPass()).thenReturn(2);
        var testResult = createTestResult(3, 2);
        var resultService = new ResultServiceImpl(testConfig, ioService);

        resultService.showResult(testResult);

        InOrder inOrder = inOrder(ioService, testConfig);
        verifyCommonResultOutput(inOrder, ioService, testResult);
        inOrder.verify(testConfig).getRightAnswersCountToPass();
        inOrder.verify(ioService).printLine("Congratulations! You passed test!");
        verifyNoMoreInteractions(testConfig, ioService);
    }

    @Test
    void shouldPrintFailedMessageWhenRightAnswersCountIsBelowPassingScore() {
        var testConfig = mock(TestConfig.class);
        var ioService = mock(IOService.class);
        when(testConfig.getRightAnswersCountToPass()).thenReturn(3);
        var testResult = createTestResult(2, 2);
        var resultService = new ResultServiceImpl(testConfig, ioService);

        resultService.showResult(testResult);

        InOrder inOrder = inOrder(ioService, testConfig);
        verifyCommonResultOutput(inOrder, ioService, testResult);
        inOrder.verify(testConfig).getRightAnswersCountToPass();
        inOrder.verify(ioService).printLine("Sorry. You fail test.");
        verifyNoMoreInteractions(testConfig, ioService);
    }

    private TestResult createTestResult(int answersCount, int rightAnswersCount) {
        var testResult = new TestResult(new Student("Ivan", "Ivanov"));
        for (int questionNumber = 1; questionNumber <= answersCount; questionNumber++) {
            testResult.applyAnswer(new Question("Question " + questionNumber, List.of()),
                    questionNumber <= rightAnswersCount);
        }
        return testResult;
    }

    private void verifyCommonResultOutput(InOrder inOrder, IOService ioService, TestResult testResult) {
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printLine("Test results: ");
        inOrder.verify(ioService).printFormattedLine("Student: %s", "Ivan Ivanov");
        inOrder.verify(ioService).printFormattedLine("Answered questions count: %d",
                testResult.getAnsweredQuestions().size());
        inOrder.verify(ioService).printFormattedLine("Right answers count: %d", testResult.getRightAnswersCount());
    }
}
