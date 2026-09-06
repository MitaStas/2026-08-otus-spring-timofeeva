package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final CsvQuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        AtomicInteger successAnswer = new AtomicInteger();
        questions.forEach(question -> {
            ioService.printLine(question.text());
            ioService.printLine("Choose number of answer:");
            for (int i = 0; i < question.answers().size(); i++) {
                ioService.printFormattedLine("%s. %s", i, question.answers().get(i).text());
            }
            int number = ioService.nextInt();
            if (checkAnswer(question, number)) {
                successAnswer.getAndIncrement();
            }
        });
        ioService.printLine("");
        ioService.printFormattedLine("Results: %d/%d correct", successAnswer, questions.size());

    }

    private boolean checkAnswer(Question question, int answerNumber) {
        return question.answers().get(answerNumber).isCorrect();

    }
}
