import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase moduloDescarga que implementa Runnable para permitir su ejecución en un hilo.
 * Se encarga de la descarga y procesamiento del contenido de una URL específica.
 */
public class moduloDescarga implements Runnable {
    // Atributos
    private URL url;
    private moduloPlanificacion planificador;
    private moduloAlmacenamiento almacenamiento;

    /**
     * Constructor del moduloDescarga.
     *
     * @param url               URL a descargar.
     * @param profundidadMaxima Profundidad máxima de crawling.
     * @param planificador      Instancia del planificador para añadir nuevas URLs.
     * @param almacenamiento    Instancia del almacenamiento para guardar los contenidos descargados.
     */
    public moduloDescarga(URL url, int profundidadMaxima, moduloPlanificacion planificador, moduloAlmacenamiento almacenamiento) {
        this.url = url;
        this.planificador = planificador;
        this.almacenamiento = almacenamiento;
    }

    /**
     * Verifica si una URL es interna (pertenece a Wikipedia).
     *
     * @param url URL a verificar.
     * @return true si la URL es interna, false en caso contrario.
     */
    private boolean mismoDominio(URL url) {
        return url.getHost().endsWith("wikipedia.org");
    }

    /**
     * Procesa el contenido descargado de la URL, extrayendo nuevas URLs y añadiéndolas al planificador.
     *
     * @param contenido Contenido de la página descargada.
     */
    private void procesarContenido(String contenido) {
        Pattern pattern = Pattern.compile("href=\"(/wiki/[^:()]*?)\""); // Ruta relativa de la URL de la página de Wikipedia
        Matcher matcher = pattern.matcher(contenido); // Busca todas las coincidencias de la expresión regular en el contenido de la página

        while (matcher.find()) { // Mientras haya coincidencias, se añaden las URLs al planificador
            try {
                URL URLencontrada = new URL("https://es.wikipedia.org" + matcher.group(1)); // Se construye la URL completa, capturando el grupo 1
                // de la expresión regular
                //System.out.println("URL encontrada: " + URLencontrada);
                int profundidadActual = planificador.obtenerProfundidad(url); // Se obtiene la profundidad de la URL actual
                if (mismoDominio(URLencontrada)) { // Si la URL es interna (del mismo dominio), se añade al planificador
                    planificador.encolarURL(URLencontrada, profundidadActual + 1); // +1 porque la profundidad de la URL encontrada es 1 mayor que
                    // la actual
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método run que se ejecutará cuando el hilo comience.
     * Realiza la descarga del contenido de la URL y procesa dicho contenido.
     */
    @Override
    public void run() {
        System.out.println("Procesando URL: " + url);
        try {
            HttpURLConnection conexionHTTP = (HttpURLConnection) url.openConnection(); // Establece conexión con la URL
            conexionHTTP.setRequestMethod("GET"); // Establece el método de la petición a GET (Obtener)

            BufferedReader reader = new BufferedReader(new InputStreamReader(conexionHTTP.getInputStream()));
            // Lee el contenido de la página línea por línea y lo almacena en un StringBuilder
            String inputLine;
            StringBuilder contenido = new StringBuilder(); // Más eficiente para almacenar cadenas de caracteres que un String

            while ((inputLine = reader.readLine()) != null) { // Lee el contenido de la página línea por línea hasta que no haya más contenido
                contenido.append(inputLine); // Almacena la línea leída en el StringBuilder
                contenido.append(System.lineSeparator()); // Añade un salto de línea al final de la línea leída
            }

            reader.close();
            procesarContenido(contenido.toString()); // Procesa el contenido de la página en busca de nuevas URLs
            almacenamiento.guardarArchivo(url, contenido.toString()); // Guarda el contenido de la página en un archivo

        } catch (Exception e) {
            System.out.println("Error al conectar con la URL: " + url);
        }
    }
}
