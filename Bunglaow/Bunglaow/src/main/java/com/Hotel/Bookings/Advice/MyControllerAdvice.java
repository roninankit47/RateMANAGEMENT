package com.Hotel.Bookings.Advice;


import java.net.http.HttpHeaders;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Global exception handler using @ControllerAdvice to handle common exceptions and provide consistent error responses.
 */

@ControllerAdvice
public class MyControllerAdvice {

	/**
	 * Handle MethodArgumentNotValidException, which is triggered when method argument validation fails.
	 *
	 * @param ex      The MethodArgumentNotValidException instance.
	 * @param headers The headers for the response.
	 * @param status  The HTTP status for the response.
	 * @param request The WebRequest instance.
	 * @return ResponseEntity with error details and HTTP status code.
	 */

	@ResponseStatus
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());

		// Get all validation errors
		List<String> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());

		body.put("errors", errors);

		return new ResponseEntity<Object>(body, HttpStatus.BAD_REQUEST);

	}

	/**
	 * Handle ConstraintViolationException, which is triggered when a bean validation constraint is violated.
	 *
	 * @param ex      The ConstraintViolationException instance.
	 * @param request The WebRequest instance.
	 * @return ResponseEntity with error details and HTTP status code.
	 */

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolation(
			ConstraintViolationException ex,
			WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());

		// Get all validation errors
		List<String> errors = ex.getConstraintViolations()
				.stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());

		body.put("errors", errors);

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	

}
