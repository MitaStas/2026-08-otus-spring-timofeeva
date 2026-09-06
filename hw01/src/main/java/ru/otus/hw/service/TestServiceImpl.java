package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        printTestIntroduction();
        var questions = questionDao.findAll();
        printQuestions(questions);

    }

    private void printTestIntroduction() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
    }

    private void printQuestions(List<Question> questions) {
        questions.forEach(this::printQuestion);
    }

    private void printQuestion(Question question) {
        ioService.printLine(question.text());
        ioService.printLine("Choose number of answer:");
        for (int i = 0; i < question.answers().size(); i++) {
            ioService.printFormattedLine("%s. %s", i + 1, question.answers().get(i).text());
        }
        ioService.printLine("");
    }
}
