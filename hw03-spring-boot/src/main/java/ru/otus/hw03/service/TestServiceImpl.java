package ru.otus.hw03.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw03.dao.QuestionDao;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.domain.Student;
import ru.otus.hw03.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        for (var question: questions) {
            printQuestion(question);
            var answeredNumber = ioService.readIntForRangeWithPromptLocalized(1, question.answers().size(),
                    "TestService.choose.the.answer", "TestService.error.message.answer");
            var isAnswerValid =  question.answers().get(answeredNumber - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
            ioService.printLine("");
        }
        return testResult;
    }

    private void printQuestion(Question question) {
        ioService.printLine(question.text());
        for (int i = 0; i < question.answers().size(); i++) {
            ioService.printFormattedLine("%s. %s", i + 1, question.answers().get(i).text());
        }
    }
}
