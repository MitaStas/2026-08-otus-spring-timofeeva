package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        var resource = getResourceByFileName(fileNameProvider.getTestFileName());
        try (var inputStreamReader = new InputStreamReader(resource)) {
            List<QuestionDto> questionDtoList = new CsvToBeanBuilder<QuestionDto>(inputStreamReader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(1)
                    .build()
                    .parse();
            return questionDtoList.stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (IOException e) {
            throw new QuestionReadException("Ошибка при чтении файла", e);
        }
    }

    private InputStream getResourceByFileName(String fileName) {
        var resource = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(fileName);
        if (resource == null) {
            throw new QuestionReadException("Не найден ресурс с именем " + fileNameProvider.getTestFileName());
        }
        return resource;
    }
}
