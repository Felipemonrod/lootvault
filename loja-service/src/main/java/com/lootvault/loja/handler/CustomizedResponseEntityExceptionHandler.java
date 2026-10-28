package com.lootvault.loja.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.lootvault.loja.exceptions.ConflictException;
import com.lootvault.loja.exceptions.ExceptionResponse;
import com.lootvault.loja.exceptions.InvalidValueException;
import com.lootvault.loja.exceptions.ResourceNotFoundException;
import com.lootvault.loja.exceptions.ServiceUnavailableException;

// Tratamento central das exceções: toda exceção lançada pelos controllers
// passa por aqui e vira um JSON padronizado (ExceptionResponse).
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
		return build(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(Exception ex, WebRequest request) {
		return build(ex, request, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidValueException.class)
	public final ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception ex, WebRequest request) {
		return build(ex, request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConflictException.class)
	public final ResponseEntity<ExceptionResponse> handleConflictExceptions(Exception ex, WebRequest request) {
		return build(ex, request, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServiceUnavailableException.class)
	public final ResponseEntity<ExceptionResponse> handleServiceUnavailableExceptions(Exception ex, WebRequest request) {
		return build(ex, request, HttpStatus.SERVICE_UNAVAILABLE);
	}

	private ResponseEntity<ExceptionResponse> build(Exception ex, WebRequest request, HttpStatus status) {
		ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(response, status);
	}
}
