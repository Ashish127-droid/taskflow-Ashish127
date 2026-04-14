package com.taskflow.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
		String reason = ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString();
		return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", reason));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
		String message = ex.getMessage() != null ? ex.getMessage() : "Error";
		HttpStatus status = switch (message) {
			case "Email already exists" -> HttpStatus.CONFLICT;
			case "Invalid credentials" -> HttpStatus.UNAUTHORIZED;
			default -> HttpStatus.INTERNAL_SERVER_ERROR;
		};
		return ResponseEntity.status(status).body(Map.of("error", message));
	}
}
