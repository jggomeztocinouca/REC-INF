import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: Comentar y generar Javadoc.

/**
 * Clase para la indexación de documentos procesados.
 */
public class Indexador {
    private final Map<String, Map<String, Double>> tfPorDocumento;
    private final Map<String, Double> idfPorTermino;
    private final Map<String, Double> longitudPorDocumento;
    private final Map<String, IndiceInvertidoEntrada> indiceInvertido;

    public Indexador() {
        this.tfPorDocumento = new ConcurrentHashMap<>();
        this.idfPorTermino = new ConcurrentHashMap<>();
        this.longitudPorDocumento = new ConcurrentHashMap<>();
        this.indiceInvertido = new ConcurrentHashMap<>();
    }

    public void indexar() {
        System.out.println("[INDEXACIÓN] Iniciando...");
        long tiempoInicio = System.currentTimeMillis();

        procesarDocumentos();
        calcularIdfYActualizarIndice();
        calcularLongitudPorDocumento();
        guardarResultados();

        long tiempoFin = System.currentTimeMillis();
        System.out.println("[INDEXACIÓN] Finalizada en " + (tiempoFin - tiempoInicio) + " ms.");
    }

    private void procesarDocumentos() {
        try (Stream<Path> paths = Files.walk(Paths.get(Rutas.RUTA_CORPUS_PROCESADO))) {
            paths.filter(Files::isRegularFile)
                    .parallel()
                    .forEach(this::procesarDocumento);
        } catch (IOException e) {
            System.err.println("[INDEXACIÓN (ERROR)] Error durante el procesamiento de documentos: " + e.getMessage());
        }
    }

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

    private void actualizarIndiceInvertido(String termino, String documento, double tf) {
        indiceInvertido.computeIfAbsent(termino, k -> new IndiceInvertidoEntrada(0.0, new ConcurrentHashMap<>()))
                .getDocumentosConPeso().put(documento, tf);
    }

    private void calcularIdfYActualizarIndice() {
        int totalDocumentos = tfPorDocumento.size();
        indiceInvertido.forEach((termino, entrada) -> {
            double idf = Math.log((double) totalDocumentos / entrada.getDocumentosConPeso().size());
            idfPorTermino.put(termino, idf);
            entrada.setIdf(idf);
            actualizarPesosDocumentos(entrada);
        });
    }

    private void actualizarPesosDocumentos(IndiceInvertidoEntrada entrada) {
        entrada.getDocumentosConPeso().forEach((documento, tf) -> {
            double tfIdf = tf * entrada.getIdf();
            entrada.getDocumentosConPeso().put(documento, tfIdf);
        });
    }

    private void calcularLongitudPorDocumento() {
        tfPorDocumento.forEach((documento, tfTerminos) -> {
            double longitud = Math.sqrt(tfTerminos.values().stream()
                    .mapToDouble(peso -> peso * peso)
                    .sum());
            longitudPorDocumento.put(documento, longitud);
        });
    }

    private void guardarResultados() {
        try {
            guardarIndiceInvertido();
            guardarIdf();
            guardarLongitudDocumentos();
        } catch (IOException e) {
            System.err.println("[INDEXACIÓN (ERROR)] Error al guardar resultados: " + e.getMessage());
        }
    }

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

    private void guardarIdf() throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(Rutas.RUTA_IDF)))) {
            idfPorTermino.forEach((termino, idf) -> writer.println(termino + ":" + idf));
        }
    }

    private void guardarLongitudDocumentos() throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(Rutas.RUTA_LONGITUD_DOCUMENTOS)))) {
            longitudPorDocumento.forEach((documento, longitud) -> writer.println(documento + ":" + longitud));
        }
    }
}
