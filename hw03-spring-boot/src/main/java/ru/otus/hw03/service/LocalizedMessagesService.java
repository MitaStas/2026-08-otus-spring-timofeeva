package ru.otus.hw03.service;

public interface LocalizedMessagesService {
    String getMessage(String code, Object ...args);
}
