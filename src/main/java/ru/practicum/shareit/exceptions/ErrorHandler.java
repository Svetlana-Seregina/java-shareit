package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e
            , HttpServletRequest request) {
        log.warn("Ошибка валидации полей объекта: " + e.getFieldError().getDefaultMessage()
                +"\nПуть запроса: " + request.getServletPath());
        return new ResponseEntity<>(e.getFieldError().getDefaultMessage()
                + "\nПуть запроса: " + request.getServletPath(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        log.warn("Запрашиваемый объект не найден: " + e.getMessage()
                + "\nПуть запроса: " +  request.getServletPath());
        return new ErrorResponse(e.getMessage() + ". Путь запроса: " + request.getServletPath());
    }

}
