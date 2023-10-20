import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicios {
    private static Scanner cadena = new Scanner(System.in);

    public static void uno() {
        System.out.print("1. Comprobar si una cadena empieza por 'abc'.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("^abc.*"));
    }

    public static void dos() {
        System.out.print("2. Comprobar si una cadena empieza por 'abc' o 'Abc'.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("^[aA]bc.*"));
    }

    public static void tres() {
        System.out.print("3. Comprobar si una cadena no empieza por un dígito.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + !cadena.nextLine().matches("^[0-9].*"));
    }

    public static void cuatro() {
        System.out.print("4. Comprobar si una cadena no acaba con un dígito.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + !cadena.nextLine().matches(".*[0-9]$"));
    }

    public static void cinco() {
        System.out.print("5. Comprobar si una cadena solo contiene los caracteres 'l' o 'a'.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("^[la]+$"));
    }

    public static void seis() {
        System.out.print("6. Comprobar si una cadena contiene un 2 y ese no está seguido por un 6.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches(".*2[^6].*"));
    }

    public static void siete() {
        System.out.print("7. Comprobar si una cadena está formada por un mínimo de 5 letras mayúsculas o minúsculas y un máximo de 10.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("^[a-zA-Z]{5,10}$"));
    }

    public static void ocho() {
        System.out.print("8. Comprobar si una cadena es una dirección web que comience por www y sea de un servidor español.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("^www\\..+\\.es$"));
    }

    public static void nueve() {
        System.out.print("9. Comprobar si una cadena es una fecha dd/mm/yy.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[0-2])/\\d{2}$"));
    }

    public static void diez() {
        System.out.print("10. Comprobar si una cadena contiene una dirección IP.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$"));
    }

    public static void once() {
        System.out.println("11. ¿Qué expresión regular utilizarías para comprobar si un número de teléfono fijo es español?");
        System.out.println("Expresión regular utilizada: ^\\+34\\s\\d{2}\\s\\d{7}$ \nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("^\\+34\\s\\d{2}\\s\\d{7}$"));
    }

    public static void doce() {
        System.out.println("12. ¿Qué expresión regular utilizarías para comprobar el número de pedido de una empresa cuyo ID puede tener los formatos indicados?");
        System.out.println("Expresión regular utilizada: ^P(\\s|[-#])\\d{1,2}([-\\s])?\\d{4,6}$ \nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("^P(\\s|[-#])\\d{1,2}([-\\s])?\\d{4,6}$"));
    }

    public static void trece() {
        System.out.println("13. Para evitar el spam, intenta localizar posibles alteraciones de la palabra 'viagra'.");
        System.out.println("Expresión regular utilizada: ^v[i1][@a]gr[@a]$ \nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("^v[i1][@a]gr[@a]$"));
    }

    public static void catorce() {
        System.out.println("14. Descarga la página principal de la UCA y localiza a través de una expresión regular en el fichero html almacenado, todas las imágenes de la página web. (Solo se evaluará la expresión regular)");
        System.out.println("Expresión regular utilizada: <img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*> \nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>"));
    }

    public static void quince() {
        System.out.println("15. Dada la cadena proporcionada, extrae los caracteres escritos entre los <tags></tags>. (Solo se evaluará la expresión regular)");
        System.out.println("Expresión regular utilizada: <[^>]+>([^<]+)</[^>]+> \nIntroduzca la cadena a evaluar: ");
        System.out.println("Evaluación de la condición: " + cadena.nextLine().matches("<[^>]+>([^<]+)</[^>]+>"));
    }

    public static void dieciseis() {
        System.out.print("16. Elimina los símbolos (:,.;?¿¡!…”’<<>>) del texto que aparece en el fichero “EjercicioExpresiones.txt”. (Solo se evaluará la expresión regular)\nIntroduzca la cadena a evaluar: ");
        String texto = cadena.nextLine().replaceAll("[:,.;?¿¡!…”’<<>>]", "");
        System.out.println("Texto modificado: " + texto);
    }

    public static void diecisiete() {
        System.out.print("17. Quita las tildes del texto obtenido en el ejercicio anterior; reemplaza por la letra no acentuada.\nIntroduzca la cadena a evaluar: ");
        String texto = cadena.nextLine().replaceAll("[áÁ]", "a").replaceAll("[éÉ]", "e").replaceAll("[íÍ]", "i").replaceAll("[óÓ]", "o").replaceAll("[úÚ]", "u");
        System.out.println("Texto sin tildes: " + texto);
    }

    public static void dieciocho() {
        System.out.print("18. Reemplaza, del resultado obtenido en el ejercicio anterior, las palabras formadas únicamente por números por un espacio.\nIntroduzca la cadena a evaluar: ");
        String texto = cadena.nextLine().replaceAll("\\b\\d+\\b", " ");
        System.out.println("Texto modificado: " + texto);
    }

    public static void diecinueve() {
        System.out.print("19. Convierte el texto anterior a mayúsculas.\nIntroduzca la cadena a evaluar: ");
        System.out.println("Texto en mayúsculas: " + cadena.nextLine().toUpperCase());
    }

    public static void veinte() {
        System.out.print("20. Reemplaza los dobles espacios que se hayan podido crear por un único espacio.\nIntroduzca la cadena a evaluar: ");
        String texto = cadena.nextLine().replaceAll(" +", " ");
        System.out.println("Texto sin dobles espacios: " + texto);
    }
}
