package com.application.services.api;

import com.application.controllers.api.ApiRestController;
import com.application.domain.DnasReceived;
import com.application.exceptions.RestMutantValidationException;
import com.application.services.mongo.AppMongoClient;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ApiRestService implements ApiService{
	private static final Logger log = LoggerFactory.getLogger(ApiRestController.class);

	@Override
	public void validateDnasReceived(final String[] dna) throws RestMutantValidationException {
		if((dna == null) || (dna.length == 0)){
			throw new RestMutantValidationException("El listado de adn a verificar se encuentra vacío.");
		}

		//Se verifica por cada fila si corresponde con las letras indicadas.
		for(String anDna : dna){
			if (!Pattern.compile(validInputRegex()).matcher(anDna).matches()) {
				throw new RestMutantValidationException("El siguiente adn es inválido: " + anDna);
			}
		}
	}

	@Override
	public String validInputRegex() {
		// De principio a fin, sólo se permite A, o T, o C, o G; una o más veces.
		return "^([ATCG]{1,})$";
	}

	@Override
	public void saveDnasReceived(String[] dna, Boolean isMutant) throws Exception{
		try {
			AppMongoClient.getDb()
					.getCollection(DnasReceived.collectionName)
					.insertOne(
							new DnasReceived(dna, isMutant).toDocument()
					);
		}
		catch (Exception e){
			log.error("Ocurrió un error al almacenar un registro para el adn recibido.", e);
			throw e;
		}
	}

	@Override
	public String[] parseReceivedDnaList(String body){
		final String DNA_KEY = "dna";

		try {
			Map<String, List> dnaReceivedObject = new Gson().fromJson(body, Map.class);
			List<String> dnasBody = (List) dnaReceivedObject.get(DNA_KEY);
			return dnasBody.toArray(new String[dnasBody.size()]);
		}
		catch (Exception e){
			log.error("Ocurrió un error al interpretar el mensaje recibido.", e);
			throw e;
		}
	}
}
