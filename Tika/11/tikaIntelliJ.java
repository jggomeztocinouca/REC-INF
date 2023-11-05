import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import java.io.IOException;
import java.nio.file.*;

public class tikaIntelliJ {
    public static void main(String[] args) throws IOException, TikaException{
        Tika tika = new Tika();

        String contenido = // Extraer el contenido del archivo PDF
                tika.parseToString(Files.newInputStream(Paths.get("Tika.pdf")));

        System.out.println("Contenido del PDF:");
        System.out.println(contenido); // Imprimir el contenido extraido
    }
}
