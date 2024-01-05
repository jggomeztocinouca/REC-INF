import java.io.IOException;
import java.util.Scanner;

public class RI {

    public static void main(String[] args) throws IOException {
        new Preprocesador().procesarCorpus();
        new Indexador().indexar();
        new Buscador().buscar();
    }
}