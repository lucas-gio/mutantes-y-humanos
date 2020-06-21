import exceptions.MutantException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import services.MutantVerificationService
import spock.lang.Specification

/**
 * Pruebas de relacionadas a la implementación de MutantVerificationService.
 */
class MutantVerificationServiceTest extends Specification{
    protected static final Log log = LogFactory.getLog(MutantVerificationServiceTest.class)
    MutantVerificationService mutantVerificationService = new MutantVerificationService()

    def "Se prueba el correcto funcionamiento del método de verificación de adn"(){
        given: "Un array de adns a verificar SIN mutantes"
        String[] adnArray = new String[13]
        adnArray[0] = "ATGCGA"
        adnArray[1] = "CAGTGC"
        adnArray[2] = "TTATTT"
        adnArray[3] = "AGACGG"
        adnArray[4] = "GCGTCA"
        adnArray[5] = "TCACTG"
        adnArray[6] = "TTTATT"
        adnArray[7] = "asdasd"
        adnArray[8] = null
        adnArray[9] = "      "
        adnArray[10] = ""
        adnArray[11] = "ttttag"   // <------ No es mutante, porque considera sólo mayúsculas.
        adnArray[12] = "AAAagg"   // <------ No es mutante, porque considera sólo mayúsculas.

        log.info("LOG INFOOOOOOOOO")

        when: "Se llama al método"
        mutantVerificationService.verify(adnArray)

        then: "Se verifica que no hay ningún adn correspondiente a mutantes"
        notThrown(MutantException)

        when: "Se envía un array de adns a verificar CON mutantes"
        adnArray = new String[6]
        adnArray[0] = "ATGCGA"
        adnArray[1] = "CAGTGC"
        adnArray[2] = "TTATGT"
        adnArray[3] = "AGAAGG"
        adnArray[4] = "CCCCTA"  // <------ Mutante
        adnArray[5] = "TCACTG"

        mutantVerificationService.verify(adnArray)

        then:"Se obtuvo la excepción MutantException ya que se encontraron mutantes"
        thrown(MutantException)
    }
}
