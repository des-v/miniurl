package com.desireevaldes.miniurl.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MiniUrlNotFoundException.class)
    public ResponseEntity<String> handleNotFound(MiniUrlNotFoundException miniUrlNotFoundException) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOther(Exception exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
