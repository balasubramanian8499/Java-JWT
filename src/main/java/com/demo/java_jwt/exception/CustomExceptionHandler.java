package com.demo.java_jwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleMethodArgumentNotValid(Exception ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.NOT_FOUND.value(),HttpStatus.NOT_FOUND, ex.getMessage(), details);
		return ResponseEntityBuilder.build(err);
	}

	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<Object> handleRuntimeExceptions(TokenExpiredException ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, ex.getMessage(), details);
		return ResponseEntityBuilder.build(err);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundException ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST, ex.getMessage(), details);
		return ResponseEntityBuilder.build(err);
	}

}
