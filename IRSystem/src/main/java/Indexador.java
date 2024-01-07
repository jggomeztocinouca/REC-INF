import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: Revisar cálculos (Comparar con la salida de ejemplo de las diapositivas de ayuda).

/**
 * Clase responsable de indexar documentos procesados para un sistema de recuperación de información.
 * Realiza el procesamiento de documentos para calcular los términos y sus frecuencias (TF),
 * el IDF de los términos y construir un índice invertido.
 *
 * @author Jesús Gómez
 * @author Francisco Mercado
 */
public class Indexador {
    private final Map<String, Map<String, Double>> tfPorDocumento;
    private final Map<String, Double> idfPorTermino;
    private final Map<String, Double> longitudPorDocumento;
    private final Map<String, IndiceInvertidoEntrada> indiceInvertido;

    /**
     * Constructor de la clase Indexador.
     * Inicializa las estructuras de datos para almacenar TF, IDF, longitud de documentos e índice invertido.
     */
    public Indexador() {
        this.tfPorDocumento = new ConcurrentHashMap<>();
        this.idfPorTermino = new ConcurrentHashMap<>();
        this.longitudPorDocumento = new ConcurrentHashMap<>();
        this.indiceInvertido = new ConcurrentHashMap<>();
        indexar();
    }

    /**
     * Método principal para iniciar el proceso de indexación.
     * Procesa los documentos, calcula TF, IDF y construye el índice invertido.
     */
    private void indexar() {
        System.out.println("[INDEXACIÓN] Iniciando...");
        long tiempoInicio = System.currentTimeMillis();

        procesarDocumentos();
        calcularIdfYActualizarIndice();
        calcularLongitudPorDocumento();
        guardarResultados();

        long tiempoFin = System.currentTimeMillis();
        System.out.println("[INDEXACIÓN] Finalizada en " + (tiempoFin - tiempoInicio) + " ms.");
    }

    /**
     * Procesa cada documento en el corpus para calcular la frecuencia de términos y construir el índice invertido.
     */
    private void procesarDocumentos() {
        try (Stream<Path> paths = Files.walk(Paths.get(Rutas.RUTA_CORPUS_PROCESADO))) {
            paths.filter(Files::isRegularFile)
                    .parallel()
                    .forEach(this::procesarDocumento);
        } catch (IOException e) {
            System.err.println("[INDEXACIÓN (ERROR)] Error durante el procesamiento de documentos: " + e.getMessage());
        }
    }

    /**
     * Procesa un documento individual para obtener la frecuencia de términos y actualizar el índice invertido.
     *
     * @param rutaDocumento Ruta al documento a procesar.
     */
    private void procesarDocumento(Path rutaDocumento) {
        try {
            List<String> terminos = Files.readAllLines(rutaDocumento).stream()
                    .flatMap(linea -> Arrays.stream(linea.split("\\s+")))
                    .collect(Collectors.toList());

            if (!terminos.isEmpty()) {
                String nombreDocumento = rutaDocumento.getFileName().toString();
                actualizarFrecuenciasTerminos(terminos, nombreDocumento);
            } else {
                System.out.println("[INDEXACIÓN (EXCEPCIÓN)] Documento vacío: " + rutaDocumento.getFileName());
            }
        } catch (IOException e) {
            System.err.println("[INDEXACIÓN (ERROR)] Error al procesar el documento " + rutaDocumento + ": " + e.getMessage());
        }
    }

    /**
     * Actualiza la frecuencia de términos y el índice invertido con los términos del documento procesado.
     *
     * @param terminos         Lista de términos en el documento.
     * @param nombreDocumento  Nombre del documento procesado.
     */
    private void actualizarFrecuenciasTerminos(List<String> terminos, String nombreDocumento) {
        Map<String, Double> frecuenciaTerminos = new ConcurrentHashMap<>();
        Map<String, Double> tfTerminos = new ConcurrentHashMap<>();

        terminos.forEach(termino -> {
            if (!termino.trim().isEmpty()) {
                frecuenciaTerminos.merge(termino, 1.0, Double::sum);
            }
        });

        frecuenciaTerminos.forEach((termino, frecuencia) -> {
            double tf = 1 + Math.log(frecuencia);
            tfTerminos.put(termino, tf);
            actualizarIndiceInvertido(termino, nombreDocumento, tf);
        });

        tfPorDocumento.put(nombreDocumento, tfTerminos);
    }

    /**
     * Actualiza el índice invertido con el término, el documento y su frecuencia TF.
     *
     * @param termino    Término a actualizar en el índice.
     * @param documento  Documento donde aparece el término.
     * @param tf         Frecuencia TF del término en el documento.
     */
    private void actualizarIndiceInvertido(String termino, String documento, double tf) {
        indiceInvertido.computeIfAbsent(termino, k -> new IndiceInvertidoEntrada(0.0, new ConcurrentHashMap<>()))
                .getDocumentosConPeso().put(documento, tf);
    }

    /**
     * Calcula el IDF para cada término y actualiza el índice invertido con el IDF y los pesos TF-IDF.
     */
    private void calcularIdfYActualizarIndice() {
        int totalDocumentos = tfPorDocumento.size();
        indiceInvertido.forEach((termino, entrada) -> {
            double idf = Math.log((double) totalDocumentos / entrada.getDocumentosConPeso().size());
            idfPorTermino.put(termino, idf);
            entrada.setIdf(idf);
            actualizarPesosDocumentos(entrada);
        });
    }

    /**
     * Actualiza los pesos TF-IDF de los documentos en el índice invertido para un término específico.
     *
     * @param entrada Entrada del índice invertido correspondiente a un término.
     */
    private void actualizarPesosDocumentos(IndiceInvertidoEntrada entrada) {
        entrada.getDocumentosConPeso().forEach((documento, tf) -> {
            double tfIdf = tf * entrada.getIdf();
            entrada.getDocumentosConPeso().put(documento, tfIdf);
        });
    }

    /**
     * Calcula la longitud de cada documento, definida como la raíz cuadrada de la suma de los cuadrados de los pesos TF-IDF de sus términos.
     */
    private void calcularLongitudPorDocumento() {
        tfPorDocumento.forEach((documento, tfTerminos) -> {
            double longitud = Math.sqrt(tfTerminos.values().stream()
                    .mapToDouble(peso -> peso * peso)
                    .sum());
            longitudPorDocumento.put(documento, longitud);
        });
    }

    /**
     * Guarda los resultados de la indexación en archivos, incluyendo el índice invertido, IDF y longitud de documentos.
     */
    private void guardarResultados() {
        try {
            guardarIndiceInvertido();
            guardarIdf();
            guardarLongitudDocumentos();
        } catch (IOException e) {
            System.err.println("[INDEXACIÓN (ERROR)] Error al guardar resultados: " + e.getMessage());
        }
    }

    /**
     * Guarda el índice invertido en un archivo.
     *
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    private void guardarIndiceInvertido() throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(Rutas.RUTA_INDICE_INVERTIDO)))) {
            indiceInvertido.forEach((termino, entrada) -> {
                String linea = termino + " → | " + entrada.getIdf() + " | " +
                        entrada.getDocumentosConPeso().entrySet().stream()
                                .map(entry -> "(" + entry.getKey() + "-peso " + entry.getValue() + ")")
                                .collect(Collectors.joining(" "));
                writer.println(linea);
            });
        }
    }

    /**
     * Guarda los valores IDF de cada término en un archivo.
     *
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    private void guardarIdf() throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(Rutas.RUTA_IDF)))) {
            idfPorTermino.forEach((termino, idf) -> writer.println(termino + ":" + idf));
        }
    }

    /**
     * Guarda las longitudes de los documentos en un archivo.
     *
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    private void guardarLongitudDocumentos() throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(Rutas.RUTA_LONGITUD_DOCUMENTOS)))) {
            longitudPorDocumento.forEach((documento, longitud) -> writer.println(documento + ":" + longitud));
        }
    }
}
