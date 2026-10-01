package ru.otus.hw03.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw03.config.TestFileNameProvider;
import ru.otus.hw03.dao.dto.QuestionDto;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.exceptions.QuestionReadException;
import ru.otus.hw03.utils.FileUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    private final FileUtil fileUtil;

    @Override
    public List<Question> findAll() {
        var questionDtoList = parseCsvFromFile(fileNameProvider.getTestFileName());
        return questionDtoList.stream()
                .map(QuestionDto::toDomainObject)
                .toList();
    }

    private List<QuestionDto> parseCsvFromFile(String fileName) {
        var inputStream = fileUtil.getResourceByFileName(fileName);
        try (var inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return new CsvToBeanBuilder<QuestionDto>(inputStreamReader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(1)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new QuestionReadException("Ошибка при чтении файла " + fileName, e);
        }
    }
}
