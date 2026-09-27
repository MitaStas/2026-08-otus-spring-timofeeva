package ru.otus.hw03.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.stereotype.Service;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.domain.Student;
import ru.otus.hw03.domain.TestResult;
import ru.otus.hw03.service.TestService;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TestFlowService {

    private final TestService testService;

    private final ComponentFlow.Builder componentFlowBuilder;

    public TestResult executeTestFor(Student student) {
        var builder = componentFlowBuilder.clone().reset();
        var testResult = new TestResult(student);
        var questions = testService.getQuestions();
        for (var questionIndex = 0; questionIndex < questions.size(); questionIndex++) {
            var question = questions.get(questionIndex);
            builder.withSingleItemSelector(questionId(questionIndex))
                    .name(question.text())
                    .selectItems(toSelectItems(question))
                    .storeResult(true)
                    .postHandler(context -> context.getValue()
                            .map(Integer::parseInt)
                            .ifPresent(answerIndex -> {
                                var isAnswerValid = question.answers().get(answerIndex).isCorrect();
                                testResult.applyAnswer(question, isAnswerValid);
                            }))
                    .and();
        }
        builder.build().run();
        return testResult;
    }

    private List<SelectItem> toSelectItems(Question question) {
        return IntStream.range(0, question.answers().size())
                .mapToObj(answerIndex -> SelectItem.of(question.answers().get(answerIndex).text(),
                        String.valueOf(answerIndex)))
                .toList();
    }

    private String questionId(int questionIndex) {
        return "question-" + questionIndex;
    }
}
