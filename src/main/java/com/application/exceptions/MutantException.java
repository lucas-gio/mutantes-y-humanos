package com.application.exceptions;

/**
 * Excepción que se libera ante la detección de un mutante.
 */
public class MutantException extends Exception{
	public MutantException(String dna){
		super(dna);
	}
}
