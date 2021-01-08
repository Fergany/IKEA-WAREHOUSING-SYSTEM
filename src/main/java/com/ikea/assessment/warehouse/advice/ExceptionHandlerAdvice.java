package com.ikea.assessment.warehouse.advice;

import com.ikea.assessment.warehouse.exception.DataLoadException;
import com.ikea.assessment.warehouse.exception.ObjectNotFoundException;
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

    @ExceptionHandler(ObjectNotFoundException.class)
    ResponseEntity<ObjectNotFoundException> objectNotFoundExceptionHandler(ObjectNotFoundException exception) {
        return new ResponseEntity<ObjectNotFoundException>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<RuntimeException> runTimeExceptionHandler(RuntimeException exception) {
        return new ResponseEntity<RuntimeException>(exception, HttpStatus.BAD_REQUEST);
    }

}
