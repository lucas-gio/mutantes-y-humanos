package com.application.services.stats;

import com.application.domain.MutantAndHumanStat;
import com.application.domain.Stat;
import com.application.services.mongo.AppMongoClient;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MutantAndHumanStatsService implements StatsService{
	final Logger LOG = LoggerFactory.getLogger(MutantAndHumanStatsService.class);

	@Override
	public String getJsonStats() throws Exception{
		try {
			Iterable<Document> statCountersList = AppMongoClient.getDb().getCollection(Stat.collectionName).find(
					Filters.eq(Stat._id, Stat.id)
			);

			Document statCounters;
			int mutantCount = 0;
			int humanCount = 0;

			if(statCountersList.iterator().hasNext()){
				statCounters = statCountersList.iterator().next();
				mutantCount = statCounters.getInteger(Stat._mutantsQuantity, 0);
				humanCount = statCounters.getInteger(Stat._humansQuantity, 0);
			}

			BigDecimal ratio = new BigDecimal(mutantCount);

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