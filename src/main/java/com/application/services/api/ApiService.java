package com.application.services.api;

import com.application.exceptions.RestMutantValidationException;

public interface ApiService {

	/**
	 * Realiza la validación del ingreso de adns. Libera RestMutantValidationException en caso de
	 * no ser válido el ingreso.
	 * @param dna El array de adns a verificar.
	 */
	void validateDnasReceived(String[] dna) throws RestMutantValidationException;

	/*
	* Expresión regular que debe cumplirse en el ingreso de datos de adn.
	*/
	String validInputRegex();

	/**
	 * Almacena los adns recibido en la base de datos.
	 * @param dnaObject El array de adns a almacenar.
	 * @param isMutant true si es mutante.
	 */
	void saveDnasReceived(String[] dnaObject, Boolean isMutant) throws Exception;


	/**
	 * Toma el body recibido por rest, y lo procesa retornando la lista de adn.
	 * @param body El cuerpo del mensaje recibido.
	 * @return El array de adn.
	 */
	String[] parseReceivedDnaList(String body);
}
