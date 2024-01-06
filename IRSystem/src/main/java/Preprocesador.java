import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Clase para el preprocesamiento de textos de los documentos del corpus.
 */
public class Preprocesador {
    private static final Set<String> PALABRAS_VACIAS = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any",
            "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both",
            "but", "by", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing",
            "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't",
            "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself",
            "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is",
            "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no",
            "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves",
            "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so",
            "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then",
            "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those",
            "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're",
            "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while",
            "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll",
            "you're", "you've", "your", "yours", "yourself", "yourselves"
    ));

    /**
     * Procesa el corpus de documentos de forma paralela.
     */
    public void procesarCorpus() {
        System.out.println("[PRE-PROCESAMIENTO] Iniciando...");
        long tiempoInicio = System.currentTimeMillis();
        try (Stream<Path> rutasStream = Files.walk(Paths.get(Rutas.RUTA_CORPUS))) {
            List<Path> rutas = rutasStream.filter(Files::isRegularFile).toList();
            Files.createDirectories(Paths.get(Rutas.RUTA_CORPUS_PROCESADO));

            rutas.parallelStream().forEach(this::procesarDocumentoIndividual);

            long tiempoFin = System.currentTimeMillis();
            System.out.println("[PRE-PROCESAMIENTO] Finalizado en " + (tiempoFin - tiempoInicio) + " ms.");
        } catch (IOException e) {
            System.err.println("[PRE-PROCESAMIENTO (ERROR)] Error al acceder a los archivos del corpus: " + e.getMessage());
        }
    }

    private void procesarDocumentoIndividual(Path ruta) {
        try {
            List<String> terminos = preprocesarDocumento(ruta);
            if (terminos.isEmpty()) {
                System.out.println("[PRE-PROCESAMIENTO (EXCEPCIÓN)] Documento vacío: " + ruta.getFileName());
            } else {
                guardarTerminosProcesados(ruta, terminos);
            }
        } catch (IOException e) {
            System.err.println("[PRE-PROCESAMIENTO (ERROR)] Error al procesar el documento " + ruta.getFileName() + ": " + e.getMessage());
        }
    }

    private List<String> preprocesarDocumento(Path rutaDocumento) throws IOException {
        String contenido = Files.readString(rutaDocumento);
        return FiltradorTexto.filtradoCompleto(contenido, PALABRAS_VACIAS);
    }

    private void guardarTerminosProcesados(Path ruta, List<String> terminos) throws IOException {
        Files.writeString(Paths.get(Rutas.RUTA_CORPUS_PROCESADO, "procesado_" + ruta.getFileName().toString()),
                String.join(" ", terminos));
    }
}
