package ru.otus.hw.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.exceptions.NotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handeNotFoundException(NotFoundException ex) {
        String errorText = "Сущность не найдена";
        var modelAndView =  new ModelAndView("customError", "errorText", errorText);
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorText = "Невозможно удалить автора: он используется в одной или нескольких книгах";
        return new ModelAndView("customError", "errorText", errorText);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpectedException(Exception ex) {
        log.error("Unexpected error while processing request", ex);
        String errorText = "Внутренняя ошибка сервера";
        ModelAndView modelAndView = new ModelAndView("customError", "errorText", errorText);
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }

}
