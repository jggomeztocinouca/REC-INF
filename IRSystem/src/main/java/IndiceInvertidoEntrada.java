import java.util.Map;

// TODO: Comentar y generar Javadoc.

/**
 * Clase que representa una entrada en el índice invertido, asociando un término con su IDF y los documentos (con sus pesos) en los que aparece.
 */
public class IndiceInvertidoEntrada {

    private double idf;
    private final Map<String, Double> documentosConPeso;

    /**
     * Constructor de la clase.
     *
     * @param idf               IDF del término.
     * @param documentosConPeso Mapa de documentos y sus pesos asociados para este término.
     */
    public IndiceInvertidoEntrada(double idf, Map<String, Double> documentosConPeso) {
        this.idf = idf;
        this.documentosConPeso = documentosConPeso;
    }

    /**
     * Obtiene el IDF del término.
     *
     * @return Valor IDF.
     */
    public double getIdf() {
        return idf;
    }

    /**
     * Establece el IDF del término.
     *
     * @param idf Valor IDF a establecer.
     */
    public void setIdf(double idf) {
        this.idf = idf;
    }

    /**
     * Obtiene el mapa de documentos con sus pesos para este término.
     *
     * @return Mapa de documentos y pesos.
     */
    public Map<String, Double> getDocumentosConPeso() {
        return documentosConPeso;
    }
}
