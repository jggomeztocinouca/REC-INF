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
    private int profundidadMaxima;
    private moduloPlanificacion planificador;
    private moduloAlmacenamiento almacenamiento;

    /**
     * Constructor del moduloDescarga.
     *
     * @param url URL a descargar.
     * @param profundidadMaxima Profundidad máxima de crawling.
     * @param planificador Instancia del planificador para añadir nuevas URLs.
     * @param almacenamiento Instancia del almacenamiento para guardar los contenidos descargados.
     */
    public moduloDescarga(URL url, int profundidadMaxima, moduloPlanificacion planificador, moduloAlmacenamiento almacenamiento) {
        this.url = url;
        this.profundidadMaxima = profundidadMaxima;
        this.planificador = planificador;
        this.almacenamiento = almacenamiento;
    }

    /**
     * Método run que se ejecutará cuando el hilo comience.
     * Realiza la descarga del contenido de la URL y procesa dicho contenido.
     */
    @Override
    public void run() {
        System.out.println("Procesando URL: " + url);
        try {
            HttpURLConnection conexionHTTP = (HttpURLConnection) url.openConnection();
            conexionHTTP.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conexionHTTP.getInputStream()));
            String inputLine;
            StringBuilder contenido = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                contenido.append(inputLine);
                contenido.append(System.lineSeparator());
            }

            reader.close();
            procesarContenido(contenido.toString());
            almacenamiento.almacenarPagina(url, contenido.toString());

        } catch (Exception e) {
            System.out.println("Error al conectar con la URL: " + url);
        }
    }

    /**
     * Procesa el contenido descargado de la URL, extrayendo nuevas URLs y añadiéndolas al planificador.
     *
     * @param contenido Contenido de la página descargada.
     */
    private void procesarContenido(String contenido) {
        Pattern pattern = Pattern.compile("href=\"(/wiki/[^:()]*?)\"");
        Matcher matcher = pattern.matcher(contenido);

        while (matcher.find()) {
            try {
                URL URLencontrada = new URL("https://es.wikipedia.org" + matcher.group(1));
                System.out.println("URL encontrada: " + URLencontrada);
                int profundidadActual = planificador.obtenerProfundidad(url);
                if (mismoDominio(URLencontrada)) {
                    planificador.encolarURL(URLencontrada, profundidadActual + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
}
