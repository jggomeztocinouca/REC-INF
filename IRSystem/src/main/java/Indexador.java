import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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
    private final Map<String, IndiceInvertidoEntrada> indiceInvertido;

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

        try (Stream<Path> paths = Files.walk(Paths.get(RUTA_SALIDA_PREPROCESADOR))) {
            paths.filter(Files::isRegularFile)
                    .parallel() // Procesamiento paralelo
                    .forEach(this::procesarDocumento);

            calcularIdfYActualizarIndice();
            calcularLongitudPorDocumento();

            guardarIndiceInvertido();
            guardarIdf();
            guardarLongitudDocumentos();
        } catch (IOException e) {
            System.err.println("Error durante la indexación: " + e.getMessage());
        }

        long tiempoFin = System.currentTimeMillis();
        System.out.println("[INDEXACIÓN] Finalizada en " + (tiempoFin - tiempoInicio) + " ms");
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
                return;
            }

            String nombreDocumento = rutaDocumento.getFileName().toString();
            Map<String, Double> frecuenciaTerminos = new ConcurrentHashMap<>();

            // Calcular la frecuencia de cada término en el documento
            for (String termino : terminos) {
                if (!termino.trim().isEmpty()) { // Comprueba que el término no esté vacío
                    frecuenciaTerminos.merge(termino, 1.0, Double::sum);
                }
            }

            // Calcular el TF para cada término y actualizar el índice invertido provisionalmente
            Map<String, Double> tfTerminos = new ConcurrentHashMap<>();
            frecuenciaTerminos.forEach((termino, frecuencia) -> {
                double tf = 1 + Math.log(frecuencia);
                tfTerminos.put(termino, tf);

                // Actualizar el índice invertido con la frecuencia del término
                indiceInvertido.computeIfAbsent(termino, k -> new IndiceInvertidoEntrada(0.0, new ConcurrentHashMap<>()))
                        .getDocumentosConPeso().put(nombreDocumento, tf);
            });

            // Almacenar los TF de cada término para este documento
            tfPorDocumento.put(nombreDocumento, tfTerminos);
        } catch (IOException e) {
            System.err.println("Error al procesar el documento " + rutaDocumento + ": " + e.getMessage());
        }
    }


    /**
     * Calcula el IDF para cada término y actualiza el índice invertido.
     */
    private void calcularIdfYActualizarIndice() {
        int totalDocumentos = tfPorDocumento.size();
        indiceInvertido.forEach((termino, entradaAntigua) -> {
            double idf = Math.log((double) totalDocumentos / entradaAntigua.getDocumentosConPeso().size());
            idfPorTermino.put(termino, idf);

            Map<String, Double> documentosConTfIdf = new ConcurrentHashMap<>();
            entradaAntigua.getDocumentosConPeso().forEach((documento, tf) -> documentosConTfIdf.put(documento, tf * idf));

            indiceInvertido.put(termino, new IndiceInvertidoEntrada(idf, documentosConTfIdf));
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
            indiceInvertido.forEach((termino, entrada) -> {
                String idf = String.valueOf(entrada.getIdf());
                entrada.getDocumentosConPeso().forEach((documento, pesoTFIDF) -> writer.println(termino + " → | " + idf + " | (" + documento + "-peso " + pesoTFIDF + ")"));
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