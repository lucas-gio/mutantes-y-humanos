package com.application.services.mutant;

import com.application.exceptions.MutantException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class MutantVerificationService implements MutantService {
    private static final Logger log = Logger.getLogger(MutantVerificationService.class);

    @Override
    public boolean isMutant(final String[] dna) {
        boolean isMutant = false;

        try{
            verify(dna);
            verifyInVertical(dna);
            verifyInOblique(dna);
            // Verificando antes si está activo el nivel, se evita generar el string parámetro si no es necesario.
            if(log.isInfoEnabled()){ log.info("No se detectó ningún mutante en el adn ingresado."); }
        }
        catch (MutantException e){
            isMutant = true;
            if(log.isInfoEnabled()){ log.info("Se detectó el adn " + ((e!=null) ? e.getMessage() : "[]") + " como propio de un mutante."); }
        }
        catch (Exception e){
            log.error("Ocurrió un error al procesar el método de verificación de mutantes.", e);
            throw e;
        }

        return isMutant;
    }

    /**
     * Verifica si algún adn corresponde con el de un mutante.
     * @param dnaArray Un array con los adn de los seres a verificar.
     */
    private void verify(String[] dnaArray) throws MutantException {
        for(String dna : dnaArray){
            verify(dna);
        }
    }

    /**
     * Verifica si el adn corresponde con el de un mutante. En caso de corresponder libera una excepción
     * MutantException.
     * @param dna El adn a verificar.
     */
    private void verify(String dna) throws MutantException{
        if(dna == null){
            return;
        }

        try {
            if (Pattern.compile(getMutantRegex()).matcher(dna).find()) {
                throw new MutantException(dna);
            }
        }
        catch (MutantException e){
            throw e;
        }
        catch(Exception e){
            log.error("Ocurrió un error al verificar el adn " + dna);
            throw e;
        }
    }

    /**
     * Dado un array, viéndose como si fuese una matriz de NxM, toma cada columna y la convierte en una fila; es decir,
     * se realiza su trasposición. Por cada fila convertida realiza la verificación.
     * @param dnaArray El array a trasponer.
     */
    public void verifyInVertical(final String[] dnaArray) throws MutantException{
        StringBuffer column;

        try {
            final int rowsLength = dnaArray.length;
            final int columnsLength = dnaArray[0].length();

            // Por cada columna, se crea un nuevo stringbuffer...
            for (int i = 0; i < columnsLength; i++) {
                column = new StringBuffer(rowsLength);

                // ...el cual se llenará con cada elemento de esa columna.
                for (int j = 0; j < rowsLength; j++) {
                    column.append(dnaArray[j].charAt(i));
                }

                // Por último, se verifica la fila...
                verify(column.toString());
            }
        }
        catch (MutantException e){
            throw e;
        }
        catch (Exception e){
            log.error("Ocurrió un error al convertir el array de adn a horizontal");
            throw e;
        }
    }

    /**
     * Dado un array, viéndose como si fuese una matriz de NxM, toma cada conjunto de letras oblicuas y las convierte
     * a filas. Se dejan de lado las letras oblicuas que en su conjunto no suman más de las n letras a verificar.
     *  Por cada fila convertida realiza la verificación.
     * @param dnaArray El array con el cual operar.
     */
    public void verifyInOblique(final String[] dnaArray) throws MutantException{
        StringBuffer diagonalResult;

        try {
            int rowsQuantity = dnaArray.length;
            int columnsQuantity = dnaArray[0].length();

            // Por cada columna de derecha a izquierda
            for (int column = columnsQuantity - 1; column >= 0; column--) {
                diagonalResult = new StringBuffer();

                // Por cada fila, de arriba hacia abajo
                for (int row = 0; row < rowsQuantity; row++) {
                    if ((column + row) < columnsQuantity) {
                        diagonalResult.append(dnaArray[row].charAt(column + row));
                    } else {
                        break;
                    }
                }

                // Se tienen en cuenta aquellas diagonales de un tamaño igual o mayor al de la cantidad de
                // letras a verificar. Por ej, si la diagonal es T, G, C, y la cantidad a verificar es 4 (por ej. GGGG)
                // generaría consumo de tiempo de procesamiento.
                if (diagonalResult.length() >= quantityOfLettersToCheck()) {
                    verify(diagonalResult.toString());
                }
            }
            //Hasta este punto se obtuvieron los resultados hasta la esquina izquierda superior de la "matriz".

            //Ahora desciende por cada fila para obtener las diagonales faltantes.
            // Empieza por la segunda fila (i=1) ya que la primera fué considerada en el for anterior.
            for (int i = 1; i < rowsQuantity; i++) {
                diagonalResult = new StringBuffer();

                for (int j = i, columna = 0; ((j < rowsQuantity) && (columna < columnsQuantity)); j++, columna++) {
                    diagonalResult.append(dnaArray[j].charAt(columna));
                }

                if (diagonalResult.length() >= quantityOfLettersToCheck()) {
                    verify(diagonalResult.toString());
                }
            }
        }
        catch (MutantException e){
            throw e;
        }
        catch (Exception e){
            log.error("Ocurrió un error al procesar el array de oblicuo a horizontal.", e);
            throw e;
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
    private String getMutantRegex(){
        return "(.{0,}A{4}|T{4}|C{4}|G{4}.{0,})";
    }

    /**
     * Determina cuantas letras deberán tenerse en cuenta para verificar si el ser es mutante o no.
     * @return Un entero con el valor correspondiente.
     */
    public int quantityOfLettersToCheck(){
        // Cuatro letras a tener en cuenta.
        return 4;
    }

}
