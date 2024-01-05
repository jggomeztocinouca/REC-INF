import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase para la indexación de documentos procesados.
 */
public class Indexador {

    private static final String RUTA_SALIDA_PREPROCESADOR = "src/main/resources/output";
    private static final String RUTA_INDICE_INVERTIDO = "src/main/resources/indice_invertido.txt";
    private static final String RUTA_IDF = "src/main/resources/idf.txt";
    private static final String RUTA_LONGITUD_DOCUMENTOS = "src/main/resources/longitud_documentos.txt";
    private final Map<String, Map<String, Double>> tfPorDocumento;
    private final Map<String, Double> idfPorTermino;
    private final Map<String, Double> longitudPorDocumento;
    private final Map<String, Map<String, Double>> indiceInvertido;

    /**
     * Constructor para la clase Indexador.
     */
    public Indexador() {
        this.tfPorDocumento = new ConcurrentHashMap<>();
        this.idfPorTermino = new ConcurrentHashMap<>();
        this.longitudPorDocumento = new ConcurrentHashMap<>();
        this.indiceInvertido = new ConcurrentHashMap<>();
    }

    /**
     * Método principal para iniciar el proceso de indexación.
     */
    public void indexar() {
        System.out.println("[INDEXACIÓN] Iniciando...");
        long tiempoInicio = System.currentTimeMillis();
        try {
            Files.walk(Paths.get(RUTA_SALIDA_PREPROCESADOR))
                    .filter(Files::isRegularFile)
                    .forEach(this::procesarDocumento);

            calcularIdfYActualizarIndice();
            calcularLongitudPorDocumento();

            try {
                guardarIndiceInvertido();
                guardarIdf();
                guardarLongitudDocumentos();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long tiempoFin = System.currentTimeMillis();
        System.out.println("[INDEXACIÓN] Finalizada.");
        System.out.println("[INDEXACIÓN] Tiempo de ejecución: " + (tiempoFin - tiempoInicio) + " ms");
    }

    /**
     * Procesa un documento individual para calcular TF y construir el índice invertido.
     *
     * @param rutaDocumento Ruta al documento a procesar.
     */
    private void procesarDocumento(Path rutaDocumento) {
        try {
            List<String> terminos = Files.readAllLines(rutaDocumento).stream()
                    .flatMap(linea -> Arrays.stream(linea.split("\\s+")))
                    .toList();

            if (terminos.isEmpty()) {
                System.out.println("Documento vacío: " + rutaDocumento.getFileName());
            }

            String nombreDocumento = rutaDocumento.getFileName().toString();
            Map<String, Double> frecuenciaTerminos = new HashMap<>();

            for (String termino : terminos) {
                if (!termino.trim().isEmpty()) { // Comprueba que el término no esté vacío
                    frecuenciaTerminos.merge(termino, 1.0, Double::sum);
                }
            }

            Map<String, Double> tfTerminos = new HashMap<>();
            frecuenciaTerminos.forEach((termino, frecuencia) -> {
                double tf = 1 + Math.log(frecuencia);
                tfTerminos.put(termino, tf);
                indiceInvertido.computeIfAbsent(termino, k -> new ConcurrentHashMap<>()).put(nombreDocumento, tf);
                //actualizarIndiceInvertido(termino, nombreDocumento, tf, idf);
            });

            tfPorDocumento.put(nombreDocumento, tfTerminos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //private void actualizarIndiceInvertido(String termino, String documento, double tf, double idf) {
      // double tfidf = tf * idf;
      // indiceInvertido.computeIfAbsent(termino, k -> new ConcurrentHashMap<>()).put(documento, tfidf);
   // }

    /**
     * Calcula el IDF para cada término y actualiza el índice invertido.
     */
    private void calcularIdfYActualizarIndice() {
        int totalDocumentos = tfPorDocumento.size();
        indiceInvertido.forEach((termino, documentos) -> {
            double idf = Math.log((double) totalDocumentos / documentos.size());
            idfPorTermino.put(termino, idf);

            documentos.forEach((documento, tf) -> documentos.put(documento, tf * idf));
        });
    }

    /**
     * Calcula la longitud de cada documento basada en el peso TF-IDF de sus términos.
     */
    private void calcularLongitudPorDocumento() {
        tfPorDocumento.forEach((documento, tfTerminos) -> {
            double longitud = tfTerminos.values().stream()
                    .mapToDouble(peso -> peso * peso)
                    .sum();
            longitudPorDocumento.put(documento, Math.sqrt(longitud));
        });
    }

    /**
     * Guarda el índice invertido en un archivo.
     */
    private void guardarIndiceInvertido() throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(RUTA_INDICE_INVERTIDO)))) {
            indiceInvertido.forEach((termino, documentos) -> {
                documentos.forEach((documento, pesoTFIDF) -> {
                    writer.println(documento + ":" + termino + "=" + pesoTFIDF);
                });
            });
        }
    }

    /**
     * Guarda los valores IDF en un archivo.
     */
    private void guardarIdf() throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(RUTA_IDF)))) {
            idfPorTermino.forEach((termino, idf) ->
                    writer.println(termino + ":" + idf));
        }
    }

    /**
     * Guarda las longitudes de los documentos en un archivo.
     */
    private void guardarLongitudDocumentos() throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(RUTA_LONGITUD_DOCUMENTOS)))) {
            longitudPorDocumento.forEach((documento, longitud) ->
                    writer.println(documento + ":" + longitud));
        }
    }

}
