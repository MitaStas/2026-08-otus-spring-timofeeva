package ru.otus.hw03.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw03.domain.Student;
import ru.otus.hw03.domain.TestResult;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestRunnerServiceImplTest {

    @Mock
    private TestService testService;

    @Mock
    private StudentService studentService;

    @Mock
    private ResultService resultService;

    @Test
    void shouldDetermineStudentRunTestAndShowResult() {
        var student = new Student("Ivan", "Ivanov");
        var testResult = new TestResult(student);
        when(studentService.determineCurrentStudent()).thenReturn(student);
        when(testService.executeTestFor(student)).thenReturn(testResult);
        var service = new TestRunnerServiceImpl(testService, studentService, resultService);

        service.run(null);

        var inOrder = inOrder(studentService, testService, resultService);
        inOrder.verify(studentService).determineCurrentStudent();
        inOrder.verify(testService).executeTestFor(student);
        inOrder.verify(resultService).showResult(testResult);
    }
}
