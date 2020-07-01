package com.application.controllers.api;

import com.application.exceptions.RestMutantValidationException;
import com.application.services.api.ApiService;
import com.application.services.mutant.MutantService;
import com.application.services.stats.StatsService;
import com.application.utils.Path;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.net.HttpURLConnection;

/**
 * Controlador dedicado a los ingresos al sistema por api rest.
 */
public class ApiRestController {
	private static final Logger LOG = LoggerFactory.getLogger(ApiRestController.class);
	private ApiService apiService;
	private MutantService mutantService;
	private StatsService statsService;
	private final static int SERVER_PORT = 5000;
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

	/**
	 * Inicializa el servidor y mapea rutas a recursos.
	 */
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
	 * Procesa el ingreso de adn para verificar si es un humano o mutante.
	 * @param request El pedido del cliente.
	 * @param response La respuesta.
	 * @return Un cuerpo vacío y un estado http acorde al resultado obtenido.
	 */
	private String processMutantPost(Request request, Response response){
		try{
			String[] dnasArray = apiService.parseReceivedDna(request.body());

			apiService.validateDnaReceived(dnasArray);

			boolean isMutant = mutantService.isMutant(dnasArray);

			apiService.saveDnaReceived(dnasArray, isMutant);

			if(isMutant){
				response.status(HttpURLConnection.HTTP_OK);
			}
			else{
				response.status(HttpURLConnection.HTTP_FORBIDDEN);
			}
		}
		catch (RestMutantValidationException e){
			if(LOG.isInfoEnabled()){ LOG.info("Se detectó un error de validación sobre los adn obtenidos." + e.getMessage()); }
			// Error de entidad no procesable, en este caso por fallo de validación (no existe en HttpURLConnection).
			response.status(422);
		}
		catch (JsonSyntaxException e){
			if(LOG.isInfoEnabled()){ LOG.info("Se detectó un error de sintaxis en el mensaje obtenido. " + e.getMessage()); }
			response.status(HttpURLConnection.HTTP_BAD_REQUEST);
		}
		catch (Exception e){
			LOG.error("Ocurrió un error al procesar el ingreso de adn via rest.", e);
			response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
		}

		return "";
	}

	/**
	 * Otorga las estadísticas de conteo de mutantes y humanos, junto con su relación.
	 * @param request El pedido del cliente.
	 * @param response La respuesta.
	 * @return Un json con las estadísticas solicitadas.
	 */
	private String processStats(Request request, Response response){
		String result;

		try{
			result = statsService.getJsonStats();
			response.status(HttpURLConnection.HTTP_OK);
		}
		catch (Exception e){
			LOG.error("Ocurrió un error al generar las estadísticas a presentar.", e);
			response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
			result = "Ocurrió un error. Por favor, verifique el log.";
		}

		return result;
	}
}
