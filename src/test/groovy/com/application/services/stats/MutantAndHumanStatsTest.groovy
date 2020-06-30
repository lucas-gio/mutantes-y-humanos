package com.application.services.stats

import com.application.domain.DnaReceived
import com.application.services.mongo.AppMongoClient

import com.mongodb.BasicDBObject
import spock.lang.Specification

/**
 * Pruebas relacionadas al servicio de estadísticas de humanos y mutantes.
 */
class MutantAndHumanStatsTest extends Specification {
	MutantAndHumanStatsService mutantAndHumanStatsService = new MutantAndHumanStatsService()

	def "Se prueba que se obtengan valores válidos al pedir estadísticas llamando a getJsonStats() "(){
		given: "Una colección de adn vacía"
		cleanAllDnaCollection()
		String[] human = new String[6]
		human[0] = "ATGCGA"
		human[1] = "CAGTGC"
		human[2] = "TTATTT"
		human[3] = "AGACGG"
		human[4] = "GCGTCA"
		human[5] = "TCACTG"

		String[] mutant1 = new String[6]
		// .--  Mutante
		mutant1[0] = "ATGCGA"
		mutant1[1] = "CAGTGC"
		mutant1[2] = "TTATGT"
		mutant1[3] = "AGAAGG"
		mutant1[4] = "CCCCTA"  //<----- Mutante
		mutant1[5] = "TCACTG"
		// ^--  Mutante

		String [] mutant2 = new String[6]
		mutant2[0] = "ATGCGA"
		mutant2[1] = "CAGTGC"
		mutant2[2] = "GTTTTT"  //<----- Mutante
		mutant2[3] = "AGACGG"
		mutant2[4] = "GCGTCA"
		mutant2[5] = "TCACTG"

		String [] mutant3 = new String[6]
		mutant3[0] = "AGGCGA"
		mutant3[1] = "CTGTGC"
		mutant3[2] = "TTATTT"
		mutant3[3] = "ATACGG"
		mutant3[4] = "GTGTCA"
		mutant3[5] = "TCACTG"
		            // ^--  Mutante

		String [] mutant4 = new String[6]
		mutant4[0] = "ATGCGA"
		mutant4[1] = "CAGTGC"
		mutant4[2] = "TTATTT"
		mutant4[3] = "AGTCGG"
		mutant4[4] = "GCGTCA"
		mutant4[5] = "TCACTG"
		               // ^--  Mutante

		when: "No hay registros preexistentes y se consulta la estadística"
		String stat = mutantAndHumanStatsService.getJsonStats()

		then: "Se obtiene el resultado esperado"
		stat == '{"count_mutant_dna":0,"count_human_dna":0,"ratio":0}'

		when: "Hay 1 humano y no hay mutantes y se consulta la estadística"
		AppMongoClient.getDb().getCollection(DnaReceived.collectionName).insertOne(
				new DnaReceived(human, false).toDocument()
		)

		stat = mutantAndHumanStatsService.getJsonStats()

		then: "Se obtiene el resultado esperado"
		stat == '{"count_mutant_dna":0,"count_human_dna":1,"ratio":0.00}'

		when: "Hay 1 mutante y no hay humanos, y se consulta la estadística"
		cleanAllDnaCollection()

		AppMongoClient.getDb().getCollection(DnaReceived.collectionName).insertOne(
				new DnaReceived(mutant1, true).toDocument()
		)

		stat = mutantAndHumanStatsService.getJsonStats()

		then: "Se obtiene el resultado esperado"
		stat == '{"count_mutant_dna":1,"count_human_dna":0,"ratio":1}'

		when: "Hay 4 mutantes y 10 humanos, y se consulta la estadística"
		cleanAllDnaCollection()

		AppMongoClient.getDb().getCollection(DnaReceived.collectionName).insertMany(
			[
                    new DnaReceived(mutant1, true).toDocument(),
                    new DnaReceived(mutant2, true).toDocument(),
                    new DnaReceived(mutant3, true).toDocument(),
                    new DnaReceived(mutant4, true).toDocument(),

                    new DnaReceived(human, false).toDocument(),
                    new DnaReceived(human, false).toDocument(),
                    new DnaReceived(human, false).toDocument(),
                    new DnaReceived(human, false).toDocument(),
                    new DnaReceived(human, false).toDocument(),
                    new DnaReceived(human, false).toDocument(),
                    new DnaReceived(human, false).toDocument(),
                    new DnaReceived(human, false).toDocument(),
                    new DnaReceived(human, false).toDocument(),
                    new DnaReceived(human, false).toDocument(),
			]
		)

		stat = mutantAndHumanStatsService.getJsonStats()

		then: "Se obtiene el resultado esperado"
		stat == '{"count_mutant_dna":4,"count_human_dna":10,"ratio":0.40}'

		cleanup: "Se eliminan los datos generados"
		cleanAllDnaCollection()
	}

	/**
	 * Borra toda la colección dna received
	 */
	private void cleanAllDnaCollection(){
		AppMongoClient.getDb().getCollection(DnaReceived.collectionName).deleteMany(new BasicDBObject())
	}

}
