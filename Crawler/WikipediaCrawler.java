import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase principal del crawler WikipediaCrawler.
 * Inicia el proceso de crawling y coordina los módulos de descarga, planificación y almacenamiento.
 */
public class WikipediaCrawler {
    // Atributos
    private moduloPlanificacion planificador;
    private ExecutorService poolHilos;
    private moduloAlmacenamiento almacenamiento;
    private AtomicInteger descargasActivas; // Contador de descargas activas (evita que el pool termine antes de que un hilo añada nuevas URLs).

    /**
     * Constructor de WikipediaCrawler.
     * Inicializa el planificador, el almacenamiento y el pool de hilos.
     */
    public WikipediaCrawler() {
        this.planificador = new moduloPlanificacion();
        this.almacenamiento = new moduloAlmacenamiento();
        this.poolHilos = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.descargasActivas = new AtomicInteger(0);
    }

    /**
     * Inicia el proceso de crawling.
     * Crea un hilo para cada descarga y espera a que todos los hilos terminen.
     *
     * @param semilla           URL de inicio del crawling.
     * @param profundidadMaxima Profundidad máxima de crawling.
     */
    public void iniciaCrawling(URL semilla, int profundidadMaxima) {
        planificador.establecerProfundidadMaxima(profundidadMaxima);
        Instant instanteInicial = Instant.now(); // Se toma el instante inicial para calcular, posteriormente, el tiempo total de crawling.
        System.out.println("Crawler iniciado...");

        planificador.encolarURL(semilla, 0);

        while (!planificador.vacia() || descargasActivas.get() > 0) { // El pool de hilos estará activo mientras haya URLs por procesar o descargas activas.
            moduloPlanificacion.tuplaProfundidadURL tupla = planificador.siguienteURL();
            if (tupla != null) { // UPDATE: No es necesario comprobar si está almacenada
                descargasActivas.incrementAndGet(); // Si se decide procesar la URL, se incrementa el contador de descargas activas.
                poolHilos.submit(() -> { // Función lambda que crea un hilo para procesar la URL.
                    new moduloDescarga(tupla.url, profundidadMaxima, planificador, almacenamiento).run();
                    descargasActivas.decrementAndGet(); // Una vez procesada la URL, se decrementa el contador de descargas activas.
                });
            }
        }

        poolHilos.shutdown();
        while (!poolHilos.isTerminated()) ; // Se espera a que se cierre correctamente el pool de hilos.

        Instant instanteFinal = Instant.now();
        long tiempoTranscurrido = Duration.between(instanteInicial, instanteFinal).toSeconds();
        System.out.println("Crawling finalizado. Tiempo total: " + tiempoTranscurrido + " segundos.");
    }

    /**
     * Verifica si una URL es válida.
     * Una URL válida es aquella que pertenece a Wikipedia en español.
     *
     * @param urlStr URL a verificar.
     * @return true si la URL es válida, false en caso contrario.
     */
    private static boolean URLvalida(String urlStr) {
        return urlStr.matches("https://es\\.wikipedia\\.org/wiki/.*");
    }

    /**
     * Método main de WikipediaCrawler.
     * Solicita al usuario la URL de inicio y la profundidad máxima de crawling.
     * Inicia el proceso de crawling.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese la URL de inicio:");
        String semillaStr = scanner.nextLine();
        if (!URLvalida(semillaStr)) {
            System.out.println("URL inválida o no pertenece a Wikipedia en español.");
            return;
        }
        System.out.println("Ingrese la profundidad máxima:");
        int profundidadMaxima = scanner.nextInt();
        scanner.close();

        try {
            URL semilla = new URL(semillaStr);
            WikipediaCrawler crawler = new WikipediaCrawler();
            crawler.iniciaCrawling(semilla, profundidadMaxima);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
