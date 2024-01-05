import java.util.Map;

public class IndiceInvertidoEntrada {
    private double idf;
    private Map<String, Double> documentosConPeso;

    public IndiceInvertidoEntrada(double idf, Map<String, Double> documentosConPeso) {
        this.idf = idf;
        this.documentosConPeso = documentosConPeso;
    }

    // Métodos get y set para idf y documentosConPeso
    public double getIdf() {
        return idf;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }

    public Map<String, Double> getDocumentosConPeso() {
        return documentosConPeso;
    }


    /*public void setDocumentosConPeso(Map<String, Double> documentosConPeso) {
        this.documentosConPeso = documentosConPeso;
    }*/
}
