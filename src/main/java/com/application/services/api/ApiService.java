package com.application.services.api;

import com.application.exceptions.RestMutantValidationException;

/**
 * Interfaz destinada a servicios de api.
 */
public interface ApiService {

	/**
	 * Realiza la validación del ingreso de adn. Libera RestMutantValidationException en caso de
	 * no ser válido el ingreso.
	 * @param dna El array de adn a verificar.
	 */
	void validateDnaReceived(String[] dna) throws RestMutantValidationException;

	/*
	* Expresión regular que debe cumplirse en el ingreso de datos de adn.
	*/
	String validInputRegex();

	/**
	 * Almacena el adn recibido en la base de datos.
	 * @param dnaObject El array de adn a almacenar.
	 * @param isMutant true si es mutante.
	 */
	void saveDnaReceived(String[] dnaObject, Boolean isMutant) throws Exception;

	/**
	 * Toma el cuerpo del mensaje recibido por rest, y lo procesa, retornando el array de adn.
	 * @param body El cuerpo del mensaje recibido.
	 * @return El array de adn.
	 */
	String[] parseReceivedDna(String body);

	/**
	 * Almacena un incremento en la tabla de estadísticas de humanos y mutantes.
	 * @param isMutant
	 */
	void saveStat(Boolean isMutant) throws Exception;
}
