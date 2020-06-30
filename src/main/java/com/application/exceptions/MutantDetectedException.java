package com.application.exceptions;

/**
 * Excepción que se libera ante la detección de un mutante.
 */
public class MutantDetectedException extends Exception{
	public MutantDetectedException(String dna){
		super(dna);
	}
}
