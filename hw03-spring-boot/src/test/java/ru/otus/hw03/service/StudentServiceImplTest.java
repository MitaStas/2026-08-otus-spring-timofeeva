package ru.otus.hw03.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private LocalizedIOService ioService;

    @Test
    void shouldRequestStudentNamesUsingLocalizedPrompts() {
        when(ioService.readStringWithPromptLocalized("StudentService.input.first.name")).thenReturn("Ivan");
        when(ioService.readStringWithPromptLocalized("StudentService.input.last.name")).thenReturn("Ivanov");
        var service = new StudentServiceImpl(ioService);

        var student = service.determineCurrentStudent();

        assertThat(student.firstName()).isEqualTo("Ivan");
        assertThat(student.lastName()).isEqualTo("Ivanov");
        verify(ioService).readStringWithPromptLocalized("StudentService.input.first.name");
        verify(ioService).readStringWithPromptLocalized("StudentService.input.last.name");
    }
}
