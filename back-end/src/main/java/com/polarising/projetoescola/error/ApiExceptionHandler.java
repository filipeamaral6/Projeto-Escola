package com.polarising.projetoescola.error;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		String message = e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage)
				.collect(Collectors.toList()).toString();
		ApiException apiException = new ApiException(message, httpStatus, ZonedDateTime.now());

		return new ResponseEntity<Object>(apiException, httpStatus);
	}
	
	public ResponseEntity<Object> handleApiRequestException(ApiRequestException e, HttpHeaders headers,
		HttpStatus status, WebRequest request) {
		HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
		String message = e.getMessage();
		ApiException apiException = new ApiException(message, httpStatus, ZonedDateTime.now());

		return new ResponseEntity<Object>(apiException, httpStatus);
	}
}
