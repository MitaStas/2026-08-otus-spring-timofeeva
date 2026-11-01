package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handeNotFoundException(NotFoundException ex) {
        String errorText = "Сущность не найдена";
        return new ModelAndView("customError", "errorText", errorText);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorText = "Невозможно удалить автора: он используется в одной или нескольких книгах";
        return new ModelAndView("customError", "errorText", errorText);
    }

}
