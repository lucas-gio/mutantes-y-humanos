package com.application.services.stats;

import com.application.domain.DnaReceived;
import com.application.domain.MutantAndHumanStat;
import com.application.services.mongo.AppMongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MutantAndHumanStatsService implements StatsService{
	final Logger LOG = LoggerFactory.getLogger(MutantAndHumanStatsService.class);

	@Override
	public String getJsonStats() throws Exception{
		try {
			MongoCollection mongoCollection = AppMongoClient.getDb().getCollection(DnaReceived.collectionName);

			long mutantCount = mongoCollection.countDocuments(
					Filters.eq(DnaReceived._isMutant, true)
			);

			// Buscando nuevamente en la base se evitan problemas de presición en el resultado que utilizando un conteo sin criterios
			// y haciendo una diferencia con el total, para obtener este valor.
			long humanCount = mongoCollection.countDocuments(
					Filters.eq(DnaReceived._isMutant, false)
			);

			BigDecimal ratio = new BigDecimal((mutantCount));

			if (humanCount > 0) {
				ratio = ratio.divide(new BigDecimal(humanCount), 2, BigDecimal.ROUND_HALF_EVEN);
			}

			// Se agrega info al nuevo objeto.
			MutantAndHumanStat stat = new MutantAndHumanStat(mutantCount, humanCount, ratio);
			String result = stat.toJson();

			return result;
		}
		catch (Exception e){
			LOG.error("Ocurrió un error al procesar las estadísticas de mutantes y humanos.", e);
			throw e;
		}
	}
}