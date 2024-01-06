import java.io.IOException;

// TODO: Comentar y generar Javadoc.

public class RI {

    public static void main(String[] args) throws IOException {
        new Preprocesador().procesarCorpus();
        new Indexador().indexar();
        new Buscador().buscar();
    }
}