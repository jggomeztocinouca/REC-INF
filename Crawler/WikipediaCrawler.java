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
     * @param semilla URL de inicio del crawling.
     * @param profundidadMaxima Profundidad máxima de crawling.
     */
    public void iniciaCrawling(URL semilla, int profundidadMaxima) {
        planificador.establecerProfundidadMaxima(profundidadMaxima);
        Instant instanteInicial = Instant.now(); // Se toma el instante inicial para calcular, posteriormente, el tiempo total de crawling.
        System.out.println("Crawler iniciado...");

        planificador.encolarURL(semilla, 0);

        while (!planificador.vacia() || descargasActivas.get() > 0) {
            moduloPlanificacion.tuplaProfundidadURL tupla = planificador.siguienteURL();
            if (tupla != null && tupla.profundidad <= profundidadMaxima && !almacenamiento.almacenada(tupla.url)) {
                descargasActivas.incrementAndGet();
                poolHilos.submit(() -> {
                    new moduloDescarga(tupla.url, profundidadMaxima, planificador, almacenamiento).run();
                    descargasActivas.decrementAndGet();
                });
            }
        }

        poolHilos.shutdown();
        while(!poolHilos.isTerminated());

        Instant instanteFinal = Instant.now();
        long tiempoTranscurrido = Duration.between(instanteInicial, instanteFinal).toSeconds();
        System.out.println("Crawling finalizado. Tiempo total: " + tiempoTranscurrido + " segundos.");
    }

    private static boolean URLvalida(String urlStr) {
        return urlStr.matches("https://es\\.wikipedia\\.org/wiki/.*");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese la URL de inicio:");
        String seedStr = scanner.nextLine();
        if (!URLvalida(seedStr)) {
            System.out.println("URL inválida o no pertenece a Wikipedia en español.");
            return;
        }
        System.out.println("Ingrese la profundidad máxima:");
        int profundidadMaxima = scanner.nextInt();
        scanner.close();

        try {
            URL semilla = new URL(seedStr);
            WikipediaCrawler crawler = new WikipediaCrawler();
            crawler.iniciaCrawling(semilla, profundidadMaxima);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
