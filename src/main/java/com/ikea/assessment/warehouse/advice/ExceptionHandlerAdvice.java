package com.ikea.assessment.warehouse.advice;

import com.ikea.assessment.warehouse.exception.DataLoadException;
import com.ikea.assessment.warehouse.exception.InsufficientStockException;
import com.ikea.assessment.warehouse.exception.ObjectNotFoundException;
import com.ikea.assessment.warehouse.service.ProductServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(DataLoadException.class)
    ResponseEntity<String> dataLoadExceptionHandler(DataLoadException exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    ResponseEntity<String> objectNotFoundExceptionHandler(ObjectNotFoundException exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientStockException.class)
    ResponseEntity<String> insufficientStockExceptionHandler(InsufficientStockException exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<String> runTimeExceptionHandler(RuntimeException exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
