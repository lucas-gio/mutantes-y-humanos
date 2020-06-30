package com.application.services.stats;

/**
 * Interfaz destinada a estadísticas.
 */
public interface StatsService {

	/**
	 * Permite obtener las estadísticas.
	 * @return Un json con las estadísticas.
	 */
	String getJsonStats() throws Exception;
}
