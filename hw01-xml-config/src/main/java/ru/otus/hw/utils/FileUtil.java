package ru.otus.hw.utils;

import java.io.InputStream;
import java.util.Objects;

public class FileUtil {

    public InputStream getResourceByFileName(String fileName) {
        var resource = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(fileName);
        Objects.requireNonNull(resource, "Не найден ресурс с именем " + fileName);
        return resource;
    }

}
