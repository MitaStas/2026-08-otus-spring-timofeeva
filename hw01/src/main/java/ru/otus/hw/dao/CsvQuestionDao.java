package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.utils.FileUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
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
        try (var inputStreamReader = new InputStreamReader(inputStream)) {
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
