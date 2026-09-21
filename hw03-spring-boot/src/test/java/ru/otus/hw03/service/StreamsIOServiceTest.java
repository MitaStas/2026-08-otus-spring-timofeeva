package ru.otus.hw03.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class StreamsIOServiceTest {

    @Test
    void shouldPrintErrorForInvalidInputAndReturnNextNumberInRange() {
        var output = new ByteArrayOutputStream();
        var service = new StreamsIOService(new PrintStream(output),
                new ByteArrayInputStream("0\n2\n".getBytes(StandardCharsets.UTF_8)));

        var number = service.readIntForRange(1, 3, "Invalid value");

        assertThat(number).isEqualTo(2);
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo("Invalid value" + System.lineSeparator());
    }

    @Test
    void shouldPrintPromptBeforeReadingString() {
        var output = new ByteArrayOutputStream();
        var service = new StreamsIOService(new PrintStream(output),
                new ByteArrayInputStream("answer\n".getBytes(StandardCharsets.UTF_8)));

        var answer = service.readStringWithPrompt("Your answer");

        assertThat(answer).isEqualTo("answer");
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo("Your answer" + System.lineSeparator());
    }
}
