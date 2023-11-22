import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Clase moduloPlanificacion para gestionar la cola de URLs en el proceso de crawling.
 * Mantiene un registro de las URLs visitadas y su profundidad en el árbol de crawling.
 */
public class moduloPlanificacion {
    private Queue<tuplaProfundidadURL> colaURL;
    private ConcurrentHashMap<URL, Integer> URLvisitadas;
    private int profundidadMaxima;

    /**
     * Constructor de moduloPlanificacion.
     * Inicializa la cola y el mapa de URLs visitadas.
     */
    public moduloPlanificacion() {
        this.colaURL = new ConcurrentLinkedQueue<>();
        this.URLvisitadas = new ConcurrentHashMap<>();
        this.profundidadMaxima = 0;
    }

    /**
     * Establece la profundidad máxima de crawling.
     *
     * @param profundidadMaxima Profundidad máxima de crawling.
     */
    public void establecerProfundidadMaxima(int profundidadMaxima) {
        this.profundidadMaxima = profundidadMaxima;
    }

    /**
     * Añade una URL a la cola si no ha sido visitada o si su profundidad actual es menor que la nueva profundidad.
     *
     * @param url URL a añadir.
     * @param profundidad Profundidad de la URL.
     */
    public synchronized void encolarURL(URL url, int profundidad) {
        if (!visitada(url) && profundidad < profundidadMaxima) {
            colaURL.add(new tuplaProfundidadURL(url, profundidad));
            URLvisitadas.put(url, profundidad);
        } else {
            System.out.println("URL ya visitada: " + url);
        }
    }

    /**
     * Obtiene y elimina la siguiente URL de la cola.
     *
     * @return El siguiente par URL-Profundidad en la cola, o null si la cola está vacía.
     */
    public tuplaProfundidadURL siguienteURL() {
        tuplaProfundidadURL tupla = colaURL.poll();
        if (tupla != null) {
            System.out.println("Desencolando URL: " + tupla.url);
        }
        return tupla;
    }

    /**
     * Verifica si la cola está vacía.
     *
     * @return true si la cola está vacía, false en caso contrario.
     */
    public boolean vacia() {
        return colaURL.isEmpty();
    }

    /**
     * Verifica si una URL ya ha sido visitada.
     *
     * @param url URL a verificar.
     * @return true si ya ha sido visitada, false en caso contrario.
     */
    public boolean visitada(URL url) {
        return URLvisitadas.containsKey(url);
    }

    /**
     * Obtiene la profundidad actual de una URL visitada.
     *
     * @param url URL cuya profundidad se consulta.
     * @return La profundidad de la URL, o -1 si no ha sido visitada.
     */
    public int obtenerProfundidad(URL url) {
        return URLvisitadas.getOrDefault(url, -1);
    }

    /**
     * Clase interna para manejar pares de URL y profundidad.
     * Esto ayuda a mantener un seguimiento de la profundidad de cada URL en el proceso de crawling.
     */
    public static class tuplaProfundidadURL {
        public URL url;
        public int profundidad;

        public tuplaProfundidadURL(URL url, int profundidad) {
            this.url = url;
            this.profundidad = profundidad;
        }
    }
}
