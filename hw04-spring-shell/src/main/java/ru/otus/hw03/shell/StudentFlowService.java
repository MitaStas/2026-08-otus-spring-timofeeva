package ru.otus.hw03.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.stereotype.Service;
import ru.otus.hw03.domain.Student;
import ru.otus.hw03.service.LocalizedMessagesServiceImpl;
import ru.otus.hw03.service.StudentService;

@Service
@RequiredArgsConstructor
public class StudentFlowService {

    private static final String FIRST_NAME_ID = "first-name";

    private static final String LAST_NAME_ID = "last-name";

    private final LocalizedMessagesServiceImpl messagesService;

    private final StudentService studentService;

    private final ComponentFlow.Builder componentFlowBuilder;

    public Student determineCurrentStudent() {
        var builder = componentFlowBuilder.clone().reset();
        var context = addStudentInputs(builder).build().run().getContext();
        return createStudent(context);
    }

    public ComponentFlow.Builder addStudentInputs(ComponentFlow.Builder builder) {
        return builder
                .withStringInput(FIRST_NAME_ID)
                .name(messagesService.getMessage("StudentService.input.first.name"))
                .storeResult(true)
                .postHandler(context -> context.put(FIRST_NAME_ID, context.getInput()))
                .and()
                .withStringInput(LAST_NAME_ID)
                .name(messagesService.getMessage("StudentService.input.last.name"))
                .storeResult(true)
                .postHandler(context -> context.put(LAST_NAME_ID, context.getInput()))
                .and();
    }

    public Student createStudent(ComponentContext<?> context) {
        return studentService.create(context.get(FIRST_NAME_ID, String.class),
                context.get(LAST_NAME_ID, String.class));
    }
}
