package ru.otus.hw03.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalizedIOServiceImplTest {

    @Mock
    private LocalizedMessagesService localizedMessagesService;

    @Mock
    private IOService ioService;

    @Test
    void shouldResolveMessagesBeforeDelegatingLocalizedOperations() {
        when(localizedMessagesService.getMessage("line.code")).thenReturn("Localized line");
        when(localizedMessagesService.getMessage("formatted.code", "value")).thenReturn("Localized formatted value");
        when(localizedMessagesService.getMessage("prompt.code")).thenReturn("Localized prompt");
        when(localizedMessagesService.getMessage("error.code")).thenReturn("Localized error");
        when(ioService.readStringWithPrompt("Localized prompt")).thenReturn("answer");
        when(ioService.readIntForRange(1, 3, "Localized error")).thenReturn(2);
        when(ioService.readIntForRangeWithPrompt(1, 3, "Localized prompt", "Localized error")).thenReturn(3);
        var service = new LocalizedIOServiceImpl(localizedMessagesService, ioService);

        service.printLineLocalized("line.code");
        service.printFormattedLineLocalized("formatted.code", "value");
        var answer = service.readStringWithPromptLocalized("prompt.code");
        var number = service.readIntForRangeLocalized(1, 3, "error.code");
        var promptedNumber = service.readIntForRangeWithPromptLocalized(1, 3, "prompt.code", "error.code");

        assertThat(answer).isEqualTo("answer");
        assertThat(number).isEqualTo(2);
        assertThat(promptedNumber).isEqualTo(3);
        verify(ioService).printLine("Localized line");
        verify(ioService).printLine("Localized formatted value");
        verify(ioService).readStringWithPrompt("Localized prompt");
        verify(ioService).readIntForRange(1, 3, "Localized error");
        verify(ioService).readIntForRangeWithPrompt(1, 3, "Localized prompt", "Localized error");
    }
}
