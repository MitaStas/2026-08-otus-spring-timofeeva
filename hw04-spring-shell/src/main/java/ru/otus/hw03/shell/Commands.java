package ru.otus.hw03.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ru.otus.hw03.service.ResultService;

@Command(group = "Application Events Commands New Way")
@RequiredArgsConstructor
public class Commands {

    private final ResultService resultService;

    private final StudentFlowService studentFlowService;

    private final TestFlowService testFlowService;

    @Command(description = "Start testing", command = "start", alias = {"s"})
    public void start() {
        var student = studentFlowService.determineCurrentStudent();
        var testResult = testFlowService.executeTestFor(student);
        resultService.showResult(testResult);
    }
}
