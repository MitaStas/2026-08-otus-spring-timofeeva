package ru.otus.hw03.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.otus.hw03.config.LocaleConfig;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class LocalizedMessagesServiceImplTest {

    @Test
    void shouldReturnEnglishMessageAndFormatArgumentsForEnglishLocale() {
        var service = new LocalizedMessagesServiceImpl(localeConfig(Locale.US), messageSource());
        var message = service.getMessage("ResultService.student", "Ivan Ivanov");
        assertThat(message).isEqualTo("Student: Ivan Ivanov");
    }

    @Test
    void shouldReturnRussianMessageAndFormatArgumentsForRussianLocale() {
        var service = new LocalizedMessagesServiceImpl(localeConfig(Locale.forLanguageTag("ru-RU")), messageSource());
        var message = service.getMessage("ResultService.student", "Иван Иванов");
        assertThat(message).isEqualTo("Студент: Иван Иванов");
    }

    private LocaleConfig localeConfig(Locale locale) {
        return () -> locale;
    }

    private ResourceBundleMessageSource messageSource() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}
