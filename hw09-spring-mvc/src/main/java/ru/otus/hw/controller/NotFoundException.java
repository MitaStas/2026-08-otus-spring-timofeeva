package ru.otus.hw.controller;

public class NotFoundException extends RuntimeException {

    NotFoundException() {
        super("Entity not found");
    }
}
