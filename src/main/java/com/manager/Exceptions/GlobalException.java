package com.manager.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResouceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResouceNotFoundException(ResouceNotFoundException ex)
    {
        ErrorResponse err = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> gandleGenericException(Exception ex)
    {
        ErrorResponse err = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}