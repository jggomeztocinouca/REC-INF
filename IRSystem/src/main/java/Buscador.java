package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase Buscador encargada de gestionar la búsqueda de términos en un conjunto de documentos.
 * Utiliza un índice invertido y la longitud de los documentos para calcular la relevancia de los
 * documentos en función de una consulta dada.
 *
 * @author Jesús Gómez
 * @author Francisco Mercado
 */
public class Buscador {
    // Conjunto de palabras vacías que se filtrarán durante el preprocesamiento. (Mismo que en Preprocesador.java, pero sin "and" y "or").
    private static final Set<String> PALABRAS_VACIAS = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "any",
            "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both",
            "but", "by", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing",
            "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't",
            "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself",
            "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is",
            "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no",
            "nor", "not", "of", "off", "on", "once", "only", "other", "ought", "our", "ours", "ourselves",
            "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so",
            "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then",
            "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those",
            "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're",
            "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while",
            "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll",
            "you're", "you've", "your", "yours", "yourself", "yourselves"
    ));
    private final Map<String, IndiceInvertidoEntrada> indiceInvertido; // Mapa para almacenar el índice invertido (término -> entrada con IDF y documentos con peso).
    private final Map<String, Double> longitudDocumentos; // Mapa para almacenar la longitud de cada documento en la colección.

    /**
     * Constructor de la clase Buscador.
     * Inicializa los mapas de índice invertido y longitud de documentos.
     * Inicia el buscador del sistema de recuperación de información.
     */
    public Buscador() {
        this.indiceInvertido = new HashMap<>();
        this.longitudDocumentos = new HashMap<>();
        try{
            cargarDatos();
        }catch (IOException e){
            System.err.println("[BUSCADOR (ERROR)] Error al cargar los datos: " + e.getMessage());
        }
        buscar();
    }

    /**
     * Carga los datos necesarios para la búsqueda desde los archivos de índice invertido y longitud de documentos.
     *
     * @throws IOException Si ocurre un error al leer los archivos.
     */
    private void cargarDatos() throws IOException {
        cargarIndiceInvertido();
        cargarLongitudesDocumentos();
    }

    /**
     * Carga el índice invertido desde un archivo.
     * Lee el archivo de índice invertido y lo almacena en el mapa indiceInvertido.
     *
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    private void cargarIndiceInvertido() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(Rutas.RUTA_INDICE_INVERTIDO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(" → \\| | \\| "); // Cada línea del archivo tiene el formato: termino → | idf | (doc1-peso1) (doc2-peso2) ...
                if (partes.length < 3) { // Si no hay al menos 3 partes, la línea no tiene el formato esperado
                    System.out.println("[BUSCADOR (EXCEPCIÓN)] Formato inesperado en la línea: " + linea);
                    continue;
                }

                String termino = partes[0];
                double idf = Double.parseDouble(partes[1]);

                Map<String, Double> documentosConPeso = Arrays.stream(partes[2].split("\\) \\(")) // Separar cada documento y su peso
                        .map(p -> p.replaceAll("[()]", ""))
                        .map(p -> p.split("-peso "))
                        .collect(Collectors.toMap(p -> p[0], p -> Double.parseDouble(p[1])));

                indiceInvertido.put(termino, new IndiceInvertidoEntrada(idf, documentosConPeso)); // Añadir entrada al índice invertido con el término, IDF y documentos con sus pesos
            }
        }
    }

    /**
     * Carga las longitudes de los documentos desde un archivo.
     * Lee el archivo de longitud de documentos y lo almacena en el mapa longitudDocumentos.
     *
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    private void cargarLongitudesDocumentos() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(Rutas.RUTA_LONGITUD_DOCUMENTOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":"); // Formato: nombreDocumento:longitud
                String nombreDocumento = partes[0].trim();
                double longitud = Double.parseDouble(partes[1]);
                longitudDocumentos.put(nombreDocumento, longitud); // Añadir documento y longitud al mapa
            }
        }
    }

    /**
     * Ejecuta el proceso de búsqueda interactiva.
     * Permite al usuario introducir consultas y muestra los resultados basados en el índice invertido.
     */
    private void buscar(){
        System.out.println("[BUSCADOR] Iniciando...");
        Scanner scanner = new Scanner(System.in);
        while (true) { // Mientras el usuario no escriba "Parar buscador" y confirme, seguirá pidiendo consultas
            System.out.println("[BUSCADOR] Introduzca los términos de la consulta (o 'Parar buscador' para finalizar):");
            String consulta = scanner.nextLine();

            if ("Parar buscador".equalsIgnoreCase(consulta)) {
                System.out.println("[BUSCADOR] ¿Está seguro de que desea parar el buscador? (S/N)");
                String respuesta = scanner.nextLine();
                if ("S".equalsIgnoreCase(respuesta)) {
                    break;
                } else {
                    continue;
                }
            }

            List<String> terminosConsulta = FiltradorTexto.filtradoCompleto(consulta, PALABRAS_VACIAS);
            // Aplica el mismo filtrado que en el proprocesador, pero usando el conjunto de palabras vacías sin "and" y "or"
            Map<String, Double> resultados = procesarConsulta(terminosConsulta);

            if (!resultados.isEmpty()) { // Si hay resultados, se pide el número de resultados a mostrar al usuario
                System.out.println("[BUSCADOR] ¿Cuántos resultados desea mostrar? (introduzca un número):");
                int numeroResultados = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer del scanner
                mostrarResultados(resultados, numeroResultados);
            }
        }
        scanner.close();
        System.out.println("[BUSCADOR] Finalizado.");
    }

    /**
     * Procesa la consulta dada para calcular las puntuaciones de los documentos.
     * Utiliza diferentes enfoques dependiendo de si la consulta contiene un único término,
     * operadores AND/OR, o es una frase.
     *
     * @param terminosConsulta Lista de términos preprocesados de la consulta.
     * @return Un mapa que asocia cada documento con su puntuación basada en la consulta.
     */
    private Map<String, Double> procesarConsulta(List<String> terminosConsulta) {
        Map<String, Double> puntuacionesDocumentos = new HashMap<>();

        if (terminosConsulta.size() == 1) { // Búsqueda de un único término
            buscarTerminoUnico(terminosConsulta.get(0), puntuacionesDocumentos);
        } else if (consultaContieneOperadores(terminosConsulta)) { // Búsquedas con operadores AND/OR
            buscarConOperadores(terminosConsulta, puntuacionesDocumentos);
        } else { // Búsqueda de frases
            buscarFrase(terminosConsulta, puntuacionesDocumentos);
        }

        return ordenarResultados(puntuacionesDocumentos);
    }

    /**
     * Realiza la búsqueda para un único término y actualiza las puntuaciones de los documentos.
     *
     * @param termino El término de búsqueda.
     * @param puntuacionesDocumentos Mapa para almacenar las puntuaciones calculadas.
     */
    private void buscarTerminoUnico(String termino, Map<String, Double> puntuacionesDocumentos) {
        if (indiceInvertido.containsKey(termino)) { // Si el término está en el índice invertido, se añaden los documentos y sus puntuaciones
            IndiceInvertidoEntrada entrada = indiceInvertido.get(termino);
            entrada.getDocumentosConPeso().forEach((doc, peso) -> {
                double puntuacion = peso / longitudDocumentos.getOrDefault(doc, 1.0);
                puntuacionesDocumentos.put(doc, puntuacion);
            });
        } else {
            System.out.println("[BUSCADOR (EXCEPCIÓN)] No se encontraron resultados para el término: " + termino);
        }
    }

    /**
     * Determina si la consulta contiene operadores AND/OR.
     *
     * @param terminosConsulta Lista de términos de la consulta.
     * @return Verdadero si la consulta contiene operadores AND/OR; de lo contrario, falso.
     */
    private boolean consultaContieneOperadores(List<String> terminosConsulta) {
        return terminosConsulta.contains("and") || terminosConsulta.contains("or");
    }

    /**
     * Realiza la búsqueda con operadores AND/OR y actualiza las puntuaciones de los documentos.
     *
     * @param terminosConsulta Lista de términos de la consulta, incluyendo operadores.
     * @param puntuacionesDocumentos Mapa para almacenar las puntuaciones calculadas.
     */
    private void buscarConOperadores(List<String> terminosConsulta, Map<String, Double> puntuacionesDocumentos) {
        System.out.println(terminosConsulta);
        Map<String, Double> resultadosTemporales = new HashMap<>();
        boolean esAnd = terminosConsulta.contains("and");

        for (String termino : terminosConsulta) {
            if (!"and".equals(termino) && !"or".equals(termino)) {
                Map<String, Double> resultadosTermino = new HashMap<>();
                buscarTerminoUnico(termino, resultadosTermino);

                if (esAnd) {
                    if (resultadosTemporales.isEmpty()) { // Si es el primer término, se añaden los resultados
                        resultadosTemporales.putAll(resultadosTermino);
                    } else { // Si no es el primer término, se calcula la intersección de los resultados
                        Map<String, Double> interseccion = new HashMap<>();
                        for (String doc : resultadosTemporales.keySet()) {
                            if (resultadosTermino.containsKey(doc)) {
                                double puntuacionCombinada = resultadosTemporales.get(doc) + resultadosTermino.get(doc);
                                interseccion.put(doc, puntuacionCombinada);
                            }
                        }
                        resultadosTemporales = interseccion;
                    }
                } else { // OR
                    for (Map.Entry<String, Double> entrada : resultadosTermino.entrySet()) { // Para todos los resultados del término
                        resultadosTemporales.merge(entrada.getKey(), entrada.getValue(), Double::sum); // Se añaden al mapa de resultados temporales (combinando sus puntuaciones)
                    }
                }
            }
        }
        puntuacionesDocumentos.putAll(resultadosTemporales);
    }

    /**
     * Realiza la búsqueda de una frase y actualiza las puntuaciones de los documentos.
     *
     * @param terminosConsulta Lista de términos que forman la frase de la consulta.
     * @param puntuacionesDocumentos Mapa para almacenar las puntuaciones calculadas.
     */
    private void buscarFrase(List<String> terminosConsulta, Map<String, Double> puntuacionesDocumentos) {
        // Obtener documentos que contienen todos los términos de la frase
        Set<String> documentosCandidatos = new HashSet<>();
        boolean esPrimerTermino = true;

        for (String termino : terminosConsulta) {
            if (indiceInvertido.containsKey(termino)) {
                if (esPrimerTermino) {
                    documentosCandidatos.addAll(indiceInvertido.get(termino).getDocumentosConPeso().keySet());
                    esPrimerTermino = false;
                } else {
                    documentosCandidatos.retainAll(indiceInvertido.get(termino).getDocumentosConPeso().keySet());
                }
            } else {
                // Si alguno de los términos no está presente, no hay coincidencia de frase
                return;
            }
        }

        // Verificar si los términos aparecen en el orden correcto en los documentos candidatos
        String fraseBuscada = String.join(" ", terminosConsulta);
        documentosCandidatos.forEach(documento -> {
            try {
                String contenidoDocumento = new String(Files.readAllBytes(Paths.get(Rutas.RUTA_CORPUS_PROCESADO, documento)));
                if (contenidoDocumento.contains(fraseBuscada)) {
                    double puntuacion = calcularPuntuacionFrase(documento, terminosConsulta);
                    puntuacionesDocumentos.put(documento, puntuacion);
                }
            } catch (IOException e) {
                System.err.println("[BUSCADOR (ERROR)] Error al leer el documento " + documento + ": " + e.getMessage());
            }
        });
    }

    /**
     * Calcula la puntuación de una frase para un documento específico.
     *
     * @param documento El documento para el que se calcula la puntuación.
     * @param terminosConsulta Lista de términos de la consulta.
     * @return La puntuación calculada para el documento.
     */
    private double calcularPuntuacionFrase(String documento, List<String> terminosConsulta) {
        double puntuacion = 0.0;
        for (String termino : terminosConsulta) {
            double pesoTermino = indiceInvertido.get(termino).getDocumentosConPeso().getOrDefault(documento, 0.0);
            puntuacion += pesoTermino;
        }
        return puntuacion / longitudDocumentos.getOrDefault(documento, 1.0);
    }

    /**
     * Ordena los resultados de la búsqueda basados en sus puntuaciones.
     *
     * @param puntuacionesDocumentos Mapa de documentos y sus puntuaciones.
     * @return Mapa ordenado de documentos basado en sus puntuaciones, de mayor a menor.
     */
    private Map<String, Double> ordenarResultados(Map<String, Double> puntuacionesDocumentos) {
        return puntuacionesDocumentos.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Muestra los resultados de la búsqueda en la consola.
     *
     * @param resultados Mapa de documentos con sus puntuaciones.
     * @param numeroResultados Número de resultados a mostrar.
     */
    private void mostrarResultados(Map<String, Double> resultados, int numeroResultados) {
        System.out.println("[BUSCADOR] Resultados de la búsqueda:");

        int contador = 0;
        for (Map.Entry<String, Double> entrada : resultados.entrySet()) {
            if (contador >= numeroResultados) {
                break;
            }

            String documentID = entrada.getKey();
            double weight = entrada.getValue();

            // Muestra la información en el formato deseado.
            String[] nombreArchivo = documentID.split("_");
            System.out.println("ID Documento: " + nombreArchivo[1] + " (Peso: " + weight + ")");
            mostrarResumen(nombreArchivo[1]);
            contador++;
        }
    }

    /**
     * Muestra el resumen del documento dado el ID del documento.
     *
     * @param documentID Identificador del documento.
     */
    public void mostrarResumen(String documentID) {
        try (BufferedReader reader = Files.newBufferedReader(obtenerRutaDocumento(documentID))) {
            String line;
            StringBuilder resumen = new StringBuilder();
            int totalChars = 0; // Contador para el total de caracteres
            boolean excede = false; // Bandera para indicar si el resumen excede los 100 caracteres

            while ((line = reader.readLine()) != null) {
                if (totalChars + line.length() > 100) {
                    // Si agregar esta línea excede los 100 caracteres, añade solo los caracteres que faltan
                    resumen.append(line.substring(0, 100 - totalChars));
                    excede = true;
                    break; // Rompe el bucle, ya que se alcanzó el límite de caracteres
                } else {
                    // Si no, agrega toda la línea y actualiza el contador de caracteres
                    resumen.append(line).append("\n");
                    totalChars += line.length() + 1; // +1 por el carácter de nueva línea
                }
            }
            // Muestra la información del resumen
            if (excede)
            {
                System.out.println("Resumen: " + resumen.toString().trim() + " [...]");
            }
            else
            {
                System.out.println("Resumen: " + resumen.toString().trim());
            }
        } catch (IOException e) {
            System.err.println("Error al leer el contenido del documento " + documentID + ": " + e.getMessage());
        }
    }

    private Path obtenerRutaDocumento(String documentID) {
        return Paths.get("src", "main", "resources", "corpus", documentID);
    }
}
