import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Clase para el preprocesamiento de textos de los documentos del corpus.
 */
public class Preprocesador {

    private static final String RUTA_CORPUS = "src/main/resources/corpus";
    private static final String RUTA_SALIDA = "src/main/resources/output";
    private static final Pattern PATRON_PUNTUACION = Pattern.compile("[^\\w\\s]|_");
    private static final Pattern PATRON_NUMEROS = Pattern.compile("\\b\\d+\\b");
    private static final Pattern PATRON_ESPACIOS_DUPLICADOS = Pattern.compile("\\s+");
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
        try (Stream<Path> rutasStream = Files.walk(Paths.get(RUTA_CORPUS))) {
            List<Path> rutas = rutasStream.filter(Files::isRegularFile)
                    .toList();
            Files.createDirectories(Paths.get(RUTA_SALIDA));

            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            //AtomicInteger contadorDocumentos = new AtomicInteger(1);

            for (Path ruta : rutas) {
                pool.submit(() -> {
                    try {
                        //int numDocumento = contadorDocumentos.getAndIncrement();
                        //System.out.println("Procesando documento Nº. " + numDocumento + ": " + ruta.getFileName());
                        List<String> terminos = preprocesarDocumento(ruta);
                        if (terminos.isEmpty()) {
                            System.out.println("Documento vacío: " + ruta.getFileName());
                        } else {
                            Files.writeString(Paths.get(RUTA_SALIDA, ruta.getFileName().toString()+"_procesado"),
                                    String.join(" ", terminos));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            pool.shutdown();
            if(pool.awaitTermination(1, TimeUnit.HOURS)){
                long tiempoFin = System.currentTimeMillis();
                System.out.println("[PRE-PROCESAMIENTO] Finalizando...");
                System.out.println("[PRE-PROCESAMIENTO] Tiempo de ejecución: " + (tiempoFin - tiempoInicio) + " ms.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Preprocesa un documento leyendo su contenido y aplicando las operaciones de preprocesamiento.
     *
     * @param rutaDocumento La ruta del documento a preprocesar.
     * @return Lista de términos preprocesados del documento.
     * @throws IOException Si ocurre un error en la lectura del archivo.
     */
    private List<String> preprocesarDocumento(Path rutaDocumento) throws IOException {
        String contenido = new String(Files.readAllBytes(rutaDocumento)); // Lee el contenido del documento.
        return preprocesarTexto(contenido);
    }

    /**
     * Preprocesa un texto aplicando diversas operaciones de limpieza y normalización.
     *
     * @param texto El texto a preprocesar.
     * @return Lista de términos preprocesados.
     */
    private List<String> preprocesarTexto(String texto) {
        String textoProcesado = texto.toLowerCase();
        textoProcesado = PATRON_PUNTUACION.matcher(textoProcesado).replaceAll(" ");
        textoProcesado = PATRON_NUMEROS.matcher(textoProcesado).replaceAll(" ");
        textoProcesado = PATRON_ESPACIOS_DUPLICADOS.matcher(textoProcesado).replaceAll(" ").trim();

        List<String> terminos = new ArrayList<>(Arrays.asList(textoProcesado.split("\\s")));
        terminos = filtrarPalabrasVacias(terminos);
        return aplicarStemming(terminos);
    }

    /**
     * Filtra palabras vacías de una lista de términos.
     *
     * @param terminos Lista de términos a filtrar.
     * @return Lista de términos sin palabras vacías.
     */
    private List<String> filtrarPalabrasVacias(List<String> terminos) {
        return terminos.stream()
                .filter(termino -> !PALABRAS_VACIAS.contains(termino))
                .collect(Collectors.toList());
    }

    /**
     * Aplica stemming a una lista de términos.
     *
     * @param terminos Lista de términos a los que aplicar stemming.
     * @return Lista de términos después de aplicar stemming.
     */
    private List<String> aplicarStemming(List<String> terminos) {
        SnowballStemmer stemmer = new porterStemmer();
        List<String> terminosProcesados = new ArrayList<>();

        for (String termino : terminos) {
            stemmer.setCurrent(termino);
            stemmer.stem();
            terminosProcesados.add(stemmer.getCurrent());
        }

        return terminosProcesados;
    }

}
