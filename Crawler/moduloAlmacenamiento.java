import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase moduloAlmacenamiento para almacenar el contenido descargado de las páginas web y gestionar un índice de URLs.
 * Utiliza un mapa concurrente para garantizar la seguridad en entornos de múltiples hilos.
 */
public class moduloAlmacenamiento {
    //private ConcurrentHashMap<URL, String> URLalmacenadas; // Almacena en una tabla hash las URLs y su contenido.

    /**
     * Constructor de moduloAlmacenamiento.
     * Inicializa el mapa concurrente para almacenar las páginas y crea el subdirectorio para el corpus a descargar.
     */
    public moduloAlmacenamiento() {
        //this.URLalmacenadas = new ConcurrentHashMap<>();
        new File("corpusDescargado").mkdirs(); // Crea el subdirectorio para el corpus a descargar.
    }

    /**
     * Guarda el contenido de una página web en un archivo.
     * El nombre del archivo se deriva de la URL de la página.
     *
     * @param url       URL de la página.
     * @param contenido Contenido de la página.
     */
    public void guardarArchivo(URL url, String contenido) {
        String ruta = "corpusDescargado" + File.separator + url.getHost() + url.getPath().replace('/', '_') + ".html";
        // Guarda el contenido en un archivo cuya ruta resulta de concatenar el nombre del host y la ruta de la URL, reemplazando las barras por guiones bajos.
        try (FileWriter writer = new FileWriter(ruta)) {
            writer.write(contenido); // Escribe el contenido en el archivo.
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + ruta);
            e.printStackTrace();
        }
    }


























    
    /**
     * Almacena el contenido de una página web.
     * Utiliza un mapa concurrente para garantizar la seguridad en entornos de múltiples hilos.
     *
     * @param url URL de la página.
     * @param contenido Contenido de la página.
     */
    /*public void almacenarPagina(URL url, String contenido) {
        URLalmacenadas.put(url, contenido);
        guardarArchivo(url, contenido);
    }*/

    /**
     * Verifica si una página ya está almacenada.
     * Esto ayuda a evitar procesar y almacenar duplicados.
     *
     * @param url URL de la página a verificar.
     * @return true si la página ya está almacenada, false en caso contrario.
     */
    /*public boolean almacenada(URL url) {
        return URLalmacenadas.containsKey(url);
    }*/
}
