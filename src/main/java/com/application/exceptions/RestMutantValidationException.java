package com.application.exceptions;

/**
 * Excepción que se libera ante la detección un error de validación de ingreso de adn.
 */
public class RestMutantValidationException extends Exception{
	public RestMutantValidationException(String message){
		super(message);
	}
}
