package services.interfaces;
public interface MutantService {
    /**
     * Determina si el adn tomado por parámetro corresponde a un mutante o no.
     * @param dna El adm correspondiente al ser a evaluar.
     * @return true si es mutante, false en caso contrario.
     */
    public boolean isMutant(String[] dna);
}
