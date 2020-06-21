package services;

import exceptions.MutantException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import services.interfaces.MutantService;

import java.util.regex.Pattern;

public class MutantVerificationService implements MutantService {
    protected static final Log log = LogFactory.getLog(MutantVerificationService.class);

    @Override
    public boolean isMutant(final String[] dna) {
        boolean isMutant = false;

        try{
            verify(dna);
            verify(convertVerticalToHorizontal(dna));
            verify(convertObliqueToHorizontal(dna));
        }
        catch (MutantException e){
            isMutant = true;
        }
        catch (Exception e){

        }

        return isMutant;
    }

    /**
     * Verifica si algún adn corresponde con el de un mutante. En caso de corresponder libera una excepción
     * MutantException.
     * @param dnaArray Un array con los adn de los seres a verificar.
     */
    protected void verify(String[] dnaArray) throws MutantException {
        Pattern pattern = Pattern.compile(getMutantRegex());

        for(String dna : dnaArray){
            if(dna != null) {
                if (pattern.matcher(dna).lookingAt()) {
                    throw new MutantException();
                }
            }
        }
    }

    /**
     * Retorna una expresión regular con aquellos valores considerados como propios de un mutante.
     * De esta manera, en caso de modificarse el patrón, no es necesario modificar el método de verificación verify.
     * Notar que hay diferenciación de mayúscula-minúscula, es decir aaaa no es válido pero sí lo es AAAA ya que el
     * requerimiento especifica valores concretos.
     *
     * Detalle de la expresión: un caracter cero o más veces; A, T, C, G, alguno de ellos repetido cuatro veces;
     * y al final un caracter cero o más veces.
     * @return String con la expresión regular.
     */
    protected String getMutantRegex(){
        return "(.{0,}A{4}|T{4}|C{4}|G{4}.{0,})";
    }

    protected String[] convertVerticalToHorizontal(final String[] dnaArray){
        return null;
    }

    protected String[] convertObliqueToHorizontal(final String[] dnaArray){
        return null;
    }
}
