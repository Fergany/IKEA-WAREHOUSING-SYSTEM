package com.ikea.assessment.warehouse.advice;

import com.ikea.assessment.warehouse.exception.DataLoadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(DataLoadException.class)
    ResponseEntity<DataLoadException> dataLoadExceptionHandler(DataLoadException exception) {
        return new ResponseEntity<DataLoadException>(exception, HttpStatus.NOT_ACCEPTABLE);
    }

}
