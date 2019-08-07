package com.polarising.projetoescola.error;

public class ApiRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5377334777759551943L;

	public ApiRequestException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	public ApiRequestException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
}
