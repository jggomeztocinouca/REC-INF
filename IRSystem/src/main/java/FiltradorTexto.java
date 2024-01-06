import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FiltradorTexto {
    private static final Pattern PATRON_PUNTUACION = Pattern.compile("[^\\w\\s]|_");
    private static final Pattern PATRON_NUMEROS = Pattern.compile("\\b\\d+\\b");
    private static final Pattern PATRON_ESPACIOS_DUPLICADOS = Pattern.compile("\\s+");

    public static String limpiarTexto(String texto) {
        return PATRON_ESPACIOS_DUPLICADOS.matcher(
                PATRON_NUMEROS.matcher(
                        PATRON_PUNTUACION.matcher(texto.toLowerCase()).replaceAll(" ")
                ).replaceAll(" ")
        ).replaceAll(" ").trim();
    }

    public static List<String> dividirEnTerminos(String texto) {
        return Arrays.asList(texto.split("\\s+"));
    }

    public static List<String> filtrarPalabrasVacias(List<String> terminos, Set<String> PALABRAS_VACIAS) {
        return terminos.stream()
                .filter(termino -> !PALABRAS_VACIAS.contains(termino))
                .collect(Collectors.toList());
    }

    public static List<String> aplicarStemming(List<String> terminos) {
        SnowballStemmer stemmer = new porterStemmer();
        return terminos.stream().map(termino -> {
            stemmer.setCurrent(termino);
            stemmer.stem();
            return stemmer.getCurrent();
        }).collect(Collectors.toList());
    }

    public static List<String> filtradoCompleto(String texto, Set<String> PALABRAS_VACIAS){
        return aplicarStemming(filtrarPalabrasVacias(dividirEnTerminos(limpiarTexto(texto)), PALABRAS_VACIAS));
    }
}
