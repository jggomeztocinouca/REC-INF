import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicios {
    // 1. Comprobar si una cadena empieza por “abc”
    boolean uno(String cadena){
        return cadena.matches("^abc.*");
    }

    // 2. Comprobar si una cadena empieza por “abc” o “Abc
    boolean dos(String cadena){
        return cadena.matches("^[aA]bc.*");
    }

    // 3. Comprobar si una cadena no empieza por un dígito
    boolean tres(String cadena){
        return cadena.matches("\\D$");
    }

    // 4. Comprobar si una cadena no acaba con un dígito
    boolean cuatro(String cadena){
        return cadena.matches("^\\D");
    }

    // 5. Comprobar si una cadena solo contiene los caracteres “l” o “a”
    boolean cinco(String cadena){
        return cadena.matches("[la]");
    }

    // 6. Comprobar si una cadena contiene un 2 y ese no está seguido por un 6
    boolean seis(String cadena){
        return !cadena.matches("^26$");
    }

    // 7. Comprobar si una cadena está formada por un mínimo de 5 letras mayúsculas o minúsculas y un máximo de 10
    boolean siete(String cadena){
        return cadena.matches("[a-zA-Z]{5,10}");
    }

    // 8. Comprobar si una cadena es una dirección web que comience por www y sea de un servidor español
    boolean ocho(String cadena){
        return cadena.matches("www\\.[a-zA-Z0-9.-]\\.es");
    }

    // 9. Comprobar si una cadena es una fecha dd/mm/yy. Comprueba que tu patrón coincida con las siguientes fechas: 25/10/83, 4/11/56, 30/6/71 y 4/3/85
    boolean nueve(String cadena){
        return cadena.matches("(0?[1-9]|[12][0-9]|3[01])/" + // Día
                // 1ª condición OR -> "0?" indica que el cero es opcional, pudiendo tener el día 1 o 2 dígitos
                // 2ª condición OR -> El día puede tener como primer dígito 1 o 2 y como segundo desde el 0 hasta el 9
                // 3ª condición OR -> Sí el día tiene como primer dígito el 0, el segundo dígito deberá ser 0 o 1.
                "(0?[1-9]|1[0-2])/" + // Mes
                // 1ª condición OR -> "0?" indica que el cero es opcional, pudiendo tener el mes 1 o 2 dígitos
                // 2ª condición OR -> El mes puede tener como primer dígito 1 o 2 y como segundo desde el 0 hasta el 9
                // 3ª condición OR -> Sí el mes tiene como primer dígito el 1, el segundo dígito deberá ser 0, 1 o 2.
                "\\d{2}"); // Año
    }

    // 10. Comprobar si una cadena contiene una dirección IP. Comprueba que tu patrón coincida con las siguientes IP: 192.168.1.1, 200.36.127.40 y 10.128.1.253
    boolean diez(String cadena) {
        return cadena.matches("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                                     "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
        /*
            - 25[0-5]: Coincide con cualquier número entre 250 y 255.
            - 2[0-4][0-9]: Coincide con cualquier número entre 200 y 249.
            - [01]?[0-9][0-9]?: Coincide con cualquier número entre 0 y 199.
            - \\.: Coincide con un punto literal.
            - El operador | funciona como un OR, permitiendo que cualquiera de estos patrones sea válido.
            - {3}: Este cuantificador indica que el patrón del octeto seguido de un punto debe repetirse exactamente tres veces.

            - (25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?): Este es el patrón para el último octeto y es idéntico al patrón para los primeros tres octetos pero sin el punto al final. Permite que el último octeto sea un número entre 0 y 255.
         */
    }

    // 11. ¿Qué expresión regular utilizarías para comprobar si un número de teléfono fijo es español? Ten en cuenta el siguiente ejemplo para realizar el patrón: +34 95 6030466
    boolean once(String cadena){
        return cadena.matches("\\+34\\s\\d{2}\\s\\d{7}");
    }

    // 12. ¿Qué expresión regular utilizarías para comprobar el número de pedido de una empresa cuyo ID puede tener los siguientes:
    //• P nn-nnnnn
    //• P-nn-nnnn
    //• P# nn nnnn
    //• P#nn-nnnn
    //• P nnnnnn
    //Siendo P el comienzo del ID, y n un número.
    boolean doce(String cadena){
        return true;
    }

    boolean trece(String cadena){
        return cadena.matches("^[]$");
    }
}
