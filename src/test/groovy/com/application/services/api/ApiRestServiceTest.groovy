package com.application.services.api

import com.application.domain.DnasReceived
import com.application.services.mongo.AppMongoClient
import com.mongodb.BasicDBObject
import org.bson.Document
import spock.lang.Specification

class ApiRestServiceTest extends Specification{
    ApiRestService apiRestService = new ApiRestService()

    def "Se verifica que al obtener un json con el adn el el cuerpo de un mensaje, se interprete correctamente como array"(){
        when: "Se envía cuatro mensajes con adn válido"
        String[] mutant1 = apiRestService.parseReceivedDnaList('{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}')
        String[] mutant2 = apiRestService.parseReceivedDnaList('{"dna":["ATGCGA","CAGTGC","GTTTTT","AGACGG","GCGTCA","TCACTG"]}')
        String[] mutant3 = apiRestService.parseReceivedDnaList('{"dna":["AGGCGA","CTGTGC","TTATTT","ATACGG","GTGTCA","TCACTG"]}')
        String[] mutant4 = apiRestService.parseReceivedDnaList('{"dna":["ATGCGA","CAGTGC","TTATTT","AGTCGG","GCGTCA","TCACTG"]}')

        then: "Cada uno de ellos se interpreta como array correctamente"
        mutant1[0] == "ATGCGA"
        mutant1[1] == "CAGTGC"
        mutant1[2] == "TTATGT"
        mutant1[3] == "AGAAGG"
        mutant1[4] == "CCCCTA"
        mutant1[5] == "TCACTG"

        mutant2[0] == "ATGCGA"
        mutant2[1] == "CAGTGC"
        mutant2[2] == "GTTTTT"
        mutant2[3] == "AGACGG"
        mutant2[4] == "GCGTCA"
        mutant2[5] == "TCACTG"

        mutant3[0] == "AGGCGA"
        mutant3[1] == "CTGTGC"
        mutant3[2] == "TTATTT"
        mutant3[3] == "ATACGG"
        mutant3[4] == "GTGTCA"
        mutant3[5] == "TCACTG"

        mutant4[0] == "ATGCGA"
        mutant4[1] == "CAGTGC"
        mutant4[2] == "TTATTT"
        mutant4[3] == "AGTCGG"
        mutant4[4] == "GCGTCA"
        mutant4[5] == "TCACTG"
    }

    def "Se prueba que se almacenen correctamente los adn con los datos requeridos para su guardado"(){
        given: "Una colección de adn vacía"
        cleanAllDnaCollection()

        and : "Dos adn recibidos"
        String[] human = new String[6]
        human[0] = "ATGCGA"
        human[1] = "CAGTGC"
        human[2] = "TTATTT"
        human[3] = "AGACGG"
        human[4] = "GCGTCA"
        human[5] = "TCACTG"

        String [] mutant = new String[6]
        mutant[0] = "ATGCGA"
        mutant[1] = "CAGTGC"
        mutant[2] = "GTTTTT"  //<----- Mutante
        mutant[3] = "AGACGG"
        mutant[4] = "GCGTCA"
        mutant[5] = "TCACTG"

        when:"Se tratan de almacenar"
        apiRestService.saveDnasReceived(human, false)
        apiRestService.saveDnasReceived(mutant, true)
        List<Document> storedDnas = AppMongoClient.getDb().getCollection(DnasReceived.collectionName).find(new BasicDBObject()).collect()

        then: "Existen en la bd"
        storedDnas != null
        storedDnas.size() == 2

        and: "Tiene un id, y un listado de adn con 6 elementos"
        storedDnas.find{Document it-> it.isMutant == false}._id != null
        storedDnas.find{Document it-> it.isMutant == false}.dna != null
        storedDnas.find{Document it-> it.isMutant == false}.dna.size() == 6
        storedDnas.find{Document it-> it.isMutant == false}.dna.containsAll( [
                                "ATGCGA",
                                "CAGTGC",
                                "TTATTT",
                                "AGACGG",
                                "GCGTCA",
                                "TCACTG"
                ] )

        storedDnas.find{Document it-> it.isMutant == true}._id != null
        storedDnas.find{Document it-> it.isMutant == true}.dna != null
        storedDnas.find{Document it-> it.isMutant == true}.dna.size() == 6
        storedDnas.find{Document it-> it.isMutant == true}.dna.containsAll([
                        "ATGCGA",
                        "CAGTGC",
                        "GTTTTT",
                        "AGACGG",
                        "GCGTCA",
                        "TCACTG"
                ])

        cleanup: "Se eliminan los datos generados"
        cleanAllDnaCollection()
    }

    /**
     * Borra toda la colección dna received
     */
    private void cleanAllDnaCollection(){
        AppMongoClient.getDb().getCollection(DnasReceived.collectionName).deleteMany(new BasicDBObject())
    }
}
