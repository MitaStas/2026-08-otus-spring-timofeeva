package ru.otus.hw03.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw03.domain.Student;
import ru.otus.hw03.domain.TestResult;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestRunnerServiceImpl.class})
class TestRunnerServiceImplTest {

    @MockitoBean
    private TestService testService;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private ResultService resultService;

    @Autowired
    private TestRunnerService service;

    @Test
    void shouldDetermineStudentRunTestAndShowResult() {
        var student = new Student("Ivan", "Ivanov");
        var testResult = new TestResult(student);
        when(studentService.determineCurrentStudent()).thenReturn(student);
        when(testService.executeTestFor(student)).thenReturn(testResult);
        service.run();
        var inOrder = inOrder(studentService, testService, resultService);
        inOrder.verify(studentService).determineCurrentStudent();
        inOrder.verify(testService).executeTestFor(student);
        inOrder.verify(resultService).showResult(testResult);
    }
}
