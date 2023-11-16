import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.*;

public class crawler {
    private moduloDescarga descarga;
    private ExecutorService descargaExecutor;
    private moduloAlmacenamiento almacenamiento;

    public crawler() {
        this.descarga = new moduloDescarga();
        this.descargaExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-2); // Número de hilos para la concurrencia
        this.almacenamiento = new moduloAlmacenamiento();
    }

    public void startCrawling(String semilla, int profundidad) {
        try {
            URL url = new URL(semilla);
            descarga.addUrl(url, 0);

            while (!descarga.isEmpty()) {
                URL currentUrl = descarga.getNextUrl();
                if (currentUrl != null) {
                    descargaExecutor.submit(new moduloDescarga(currentUrl, profundidad, descarga, almacenamiento));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Ingrese la URL de inicio:");
        String semilla = input.nextLine();
        System.out.println("Ingrese la profundidad máxima:");
        int profundidad = input.nextInt();

        crawler crawler = new crawler();
        crawler.startCrawling(semilla, profundidad);
    }
}
