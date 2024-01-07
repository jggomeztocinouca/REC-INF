// TODO: Indexador.java
// TODO: Comprobar que se cumplen todos los requisitos del proyecto
// TODO: Documentación

/**
 * Clase principal del proyecto.
 * @author Jesús Gómez
 * @author Francisco Mercado
 * @version 2024
 */
public class RI {

    /**
     * Método principal del proyecto.
     * Inicia el proceso de preprocesamiento, indexación y búsqueda.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        new Preprocesador();
        new Indexador();
        new Buscador();
    }
}