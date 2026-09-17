package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        collectResults(questions, testResult);
        return testResult;
    }

    private void collectResults(List<Question> questions, TestResult testResult) {
        for (var question : questions) {
            printQuestion(question);
            var answeredNumber = ioService.readIntForRangeWithPrompt(1, question.answers().size(),
                    "Choose number of answer:", "Invalid value. Try again");
            var isAnswerValid = question.answers().get(answeredNumber - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
            ioService.printLine("");
        }
    }

    private void printQuestion(Question question) {
        ioService.printLine(question.text());
        for (int i = 0; i < question.answers().size(); i++) {
            ioService.printFormattedLine("%s. %s", i + 1, question.answers().get(i).text());
        }
    }
}
