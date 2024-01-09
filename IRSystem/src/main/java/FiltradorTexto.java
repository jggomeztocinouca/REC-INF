package main.java;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Clase para el filtrado de texto.
 * Esta clase se encarga de aplicar una serie de filtros de texto a una cadena de texto recibida.
 *
 * @author Jesús Gómez
 * @author Francisco Mercado
 */
public class FiltradorTexto {
    private static final Pattern PATRON_PUNTUACION = Pattern.compile("[^\\w\\s]|_");
    private static final Pattern PATRON_NUMEROS = Pattern.compile("\\b\\d+\\b");
    private static final Pattern PATRON_ESPACIOS_DUPLICADOS = Pattern.compile("\\s+");

    /**
     * Método que aplica una serie de filtros de texto a una cadena de texto recibida,
     * sustituyendo caracteres no alfanuméricos, números y espacios duplicados por espacios.
     * @param texto Texto a filtrar.
     */
    public static String limpiarTexto(String texto) {
        return PATRON_ESPACIOS_DUPLICADOS.matcher(
                PATRON_NUMEROS.matcher(
                        PATRON_PUNTUACION.matcher(texto.toLowerCase()).replaceAll(" ")
                ).replaceAll(" ")
        ).replaceAll(" ").trim();
    }

    /**
     * Método que divide una cadena de texto en términos individuales.
     * @param texto Texto a dividir.
     */
    public static List<String> dividirEnTerminos(String texto) {
        return Arrays.asList(texto.split("\\s+"));
    }

    /**
     * Método que filtra una lista de términos, eliminando aquellos que se encuentren en el conjunto de palabras vacías.
     * @param terminos Lista de términos a filtrar.
     * @param PALABRAS_VACIAS Conjunto de palabras vacías.
     */
    public static List<String> filtrarPalabrasVacias(List<String> terminos, Set<String> PALABRAS_VACIAS) {
        return terminos.stream()
                .filter(termino -> !PALABRAS_VACIAS.contains(termino))
                .collect(Collectors.toList());
    }

    /**
     * Método que aplica el algoritmo de stemming de Porter a una lista de términos.
     * @param terminos Lista de términos a los que aplicar stemming.
     */
    public static List<String> aplicarStemming(List<String> terminos) {
        SnowballStemmer stemmer = new porterStemmer();
        return terminos.stream().map(termino -> {
            stemmer.setCurrent(termino);
            stemmer.stem();
            return stemmer.getCurrent();
        }).collect(Collectors.toList());
    }

    /**
     * Método que aplica todos los filtros de texto a una cadena de texto.
     * @param texto Texto a filtrar.
     * @param PALABRAS_VACIAS Conjunto de palabras vacías.
     */
    public static List<String> filtradoCompleto(String texto, Set<String> PALABRAS_VACIAS){
        return aplicarStemming(filtrarPalabrasVacias(dividirEnTerminos(limpiarTexto(texto)), PALABRAS_VACIAS));
    }
}
