package com.application.controllers.api;

import com.application.exceptions.RestMutantValidationException;
import com.application.services.api.ApiService;
import com.application.services.mutant.MutantService;
import com.application.services.stats.StatsService;
import com.application.utils.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.net.HttpURLConnection;

public class ApiRestController {
	private static final Logger log = LoggerFactory.getLogger(ApiRestController.class);
	private ApiService apiService;
	private MutantService mutantService;
	private StatsService statsService;
	private final int SERVER_PORT = 8080;
	private final int MAX_THREADS = 8;
	private final int MIN_THREADS = 2;
	private final int IDLE_TIMEOUT_MS = 30000;

	public ApiRestController(final ApiService apiService,
	                         final MutantService mutantService,
	                         final StatsService statsService){
		this.apiService = apiService;
		this.mutantService = mutantService;
		this.statsService = statsService;
		initializeRoutes();
	}

	private void initializeRoutes() {
		Spark.port(SERVER_PORT);
		Spark.threadPool(MAX_THREADS, MIN_THREADS, IDLE_TIMEOUT_MS);

		Spark.post(Path.MUTANT, (request, response) -> {
			return processMutantPost(request, response);
		});

		Spark.get(Path.STATS, (request, response) -> {
			return processStats(request, response);
		});
	}

	/**
	 * Procesa los mutantes obtenidos via rest.
	 * @param request El pedido del cliente
	 * @param response La respuesta
	 * @return En _todo caso un string vacío ya que no es necesario un cuerpo.
	 */
	private String processMutantPost(Request request, Response response){
		try{
			String[] dnasArray = apiService.parseReceivedDnaList(request.body());

			apiService.validateDnasReceived(dnasArray);

			boolean isMutant = mutantService.isMutant(dnasArray);

			apiService.saveDnasReceived(dnasArray, isMutant);

			if(isMutant){
				response.status(HttpURLConnection.HTTP_OK);
			}
			else{
				response.status(HttpURLConnection.HTTP_FORBIDDEN);
			}
		}
		catch (RestMutantValidationException e){
			if(log.isInfoEnabled()){ log.info("Se detectó un error de validación sobre los adn obtenidos." + e.getMessage()); }
			// Error de entidad no procesable, en este caso por falo de validación (no existe en HttpURLConnection).
			response.status(422);
		}
		catch (Exception e){
			log.error("Ocurrió un error al procesar el ingreso de adn via rest.", e);
			response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
		}

		return "";
	}

	/**
	 * Otorga las estadísticas de conteo de mutantes y humanos, junto con su relación.
	 * @param request El pedido del cliente
	 * @param response La respuesta
	 * @return Un json con las estadísticas solicitadas.
	 */
	private String processStats(Request request, Response response){
		String result;

		try{
			result = statsService.getJsonStats();
			response.status(HttpURLConnection.HTTP_OK);
		}
		catch (Exception e){
			log.error("Ocurrió un error al generar las estadísticas a presentar.", e);
			response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
			result = "Ocurrió un error. Por favor, verifique el log.";
		}

		return result;
	}
}
