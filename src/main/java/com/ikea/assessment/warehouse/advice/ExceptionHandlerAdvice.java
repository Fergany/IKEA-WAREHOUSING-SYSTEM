package com.ikea.assessment.warehouse.advice;

import com.ikea.assessment.warehouse.exception.DataLoadException;
import com.ikea.assessment.warehouse.exception.InsufficientStockException;
import com.ikea.assessment.warehouse.exception.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(DataLoadException.class)
    ResponseEntity<String> dataLoadExceptionHandler(DataLoadException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    ResponseEntity<String> objectNotFoundExceptionHandler(ObjectNotFoundException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientStockException.class)
    ResponseEntity<String> insufficientStockExceptionHandler(InsufficientStockException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<String> runTimeExceptionHandler(RuntimeException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
