import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicios {
    private static Scanner cadena = new Scanner(System.in);



    public static void uno() {
        System.out.print("1. Comprobar si una cadena empieza por 'abc'.\nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^abc.*");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void dos() {
        System.out.print("2. Comprobar si una cadena empieza por 'abc' o 'Abc'.\nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^[aA]bc.*");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void tres() {
        System.out.print("3. Comprobar si una cadena no empieza por un dígito.\nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^[^0-9]");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }


    public static void cuatro() {
        System.out.print("4. Comprobar si una cadena no acaba con un dígito.\nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("[^0-9]$");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void cinco() {
        System.out.print("5. Comprobar si una cadena solo contiene los caracteres 'l' o 'a'.\nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^(l+|a+)$");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void seis() {
        System.out.print("6. Comprobar si una cadena contiene un 2 y ese no está seguido por un 6.\nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("2(?!6)");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void siete() {
        System.out.print("7. Comprobar si una cadena está formada por un mínimo de 5 letras mayúsculas o minúsculas y un máximo de 10.\nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^[a-zA-Z]{5,10}$");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void ocho() {
        System.out.print("8. Comprobar si una cadena es una dirección web que comience por 'www' y sea de un servidor español ('.es').\nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^www\\..*\\.es$");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void nueve() {
        System.out.print("9. Comprobar si una cadena es una fecha dd/mm/yy.\nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[0-2])/\\d{2}$");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void diez() {
        System.out.print("10. Comprobar si una cadena contiene una dirección IP.\nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }
    public static void once() { // A PARTIR DE AQUÍ SE DESCUADRA LA ENTRADA
        System.out.println("11. ¿Qué expresión regular utilizarías para comprobar si un número de teléfono fijo es español?");
        System.out.print("Expresión regular utilizada: ^\\+34\\s\\d{2}\\s\\d{7}$ \nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^\\+34\\s\\d{2}\\s\\d{7}$");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void doce() {
        System.out.println("12. ¿Qué expresión regular utilizarías para comprobar el número de pedido de una empresa cuyo ID puede tener los formatos indicados?");
        System.out.print("Expresión regular utilizada: ^P(\\s|[-#])\\d{1,2}([-\\s])?\\d{4,6}$ \nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^P(\\s|[-#])\\d{1,2}([-\\s])?\\d{4,6}$");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void trece() {
        System.out.println("13. Para evitar el spam, intenta localizar posibles alteraciones de la palabra 'viagra'.");
        System.out.print("Expresión regular utilizada: ^v[i!1][@a]gr[@a]$ \nIntroduzca la cadena a evaluar: ");
        Pattern pattern = Pattern.compile("^v[i!1][@a]gr[@a]$");
        Matcher matcher = pattern.matcher(cadena.nextLine());
        System.out.println("Evaluación de la condición: " + matcher.find());
    }

    public static void catorce() throws IOException {
        System.out.println("14. Descarga la página principal de la UCA y localiza a través de una expresión regular en el fichero html almacenado, todas las imágenes de la página web.");
        System.out.println("Expresión regular utilizada: \"<img.*>\"");

        URL url = new URL("https://www.uca.es/");
        URLConnection conexion = url.openConnection();
        BufferedReader lectorHTML = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

        Pattern pattern = Pattern.compile("<img.*>");

        String cadena;
        while ((cadena = lectorHTML.readLine()) != null) {
            Matcher matcher = pattern.matcher(cadena);
            if (matcher.find()) {
                System.out.println(cadena);
            }
        }
    }

    public static void quince() {
        System.out.println("15. Dada la cadena proporcionada, extrae los caracteres escritos entre los <tags></tags> mediante los 3 patrones propuestos.");
        String cadena = "<a>uno</a><b>dos</b><c>tres</c><d>cuatro</d><e>cinco</e>";

        String patron1 = "<[^>]*>([^<]*)</[^>]*>";
        Pattern pattern = Pattern.compile(patron1);
        Matcher matcher = pattern.matcher(cadena);

        System.out.println("\nPatrón 1: " + patron1);
        System.out.print("Resultado 1: ");
        while (matcher.find()) {
            System.out.print(matcher.group(1) + " ");
        }
        // System.out.println("\nExplicación 1: Este patrón es de tipo greedy y resulta correcto, por lo que extrae correctamente los caracteres indicados.");

        String patron2 = "<.*>(.*)<\\/.*>";
        pattern = Pattern.compile(patron2);
        matcher = pattern.matcher(cadena);

        System.out.println("\n\nPatrón 2: " + patron2);
        System.out.print("Resultado 2: ");
        while (matcher.find()) {
            System.out.print(matcher.group(1) + " ");
        }
        // System.out.println("\nExplicación 2: Este patrón también es de tipo greedy pero este no cumple con el objetivo indicado. El '.*' intenta extraer todo lo que puede, incluidas las etiquetas, lo que lleva a capturar solo el último valor (\"cinco\") en la cadena.");

        String patron3 = ".*?>(.*?)<\\/.*?>";
        pattern = Pattern.compile(patron3);
        matcher = pattern.matcher(cadena);

        System.out.println("\n\nPatrón 3: " + patron3);
        System.out.print("Resultado 3: ");
        while (matcher.find()) {
            System.out.print(matcher.group(1) + " ");
        }
        // System.out.println("\nExplicación 3: Este patrón es de tipo ungreedy. El '?' tras el '*' en '.*?' y .*? hace que la coincidencia sea lo más pequeña posible.");
    }


    public static void dieciseis() throws IOException {
        System.out.print("16. Elimina los símbolos (:,.;?¿¡!…”’<<>>) del texto que aparece en el fichero 'EjercicioExpresiones.txt'.\n");
        File file = new File("EjercicioExpresiones.txt");
        FileReader reader = new FileReader(file);
        BufferedReader buffer = new BufferedReader(reader);
        FileWriter writer = new FileWriter("ejercicio16.txt");
        String line = null;
        Pattern patron = Pattern.compile("[:,.;?¿¡!...\"'<<>>]");
        while((line = buffer.readLine()) != null){
            Matcher mat = patron.matcher(line);
            line = mat.replaceAll("");
            System.out.println(line);
            writer.write(line);
            writer.write("\n");
        }
        reader.close();
        writer.close();
    }

    public static void diecisiete() throws IOException {
        System.out.print("17. Quita las tildes del texto obtenido en el ejercicio anterior; reemplaza por la letra no acentuada.\n");
        File file = new File("ejercicio16.txt");
        FileReader reader = new FileReader(file);
        BufferedReader buffer = new BufferedReader(reader);
        FileWriter writer = new FileWriter("ejercicio17.txt");
        String line = null;
        Pattern patron = Pattern.compile("[:,.;?¿¡!...\"'<<>>]");
        while((line = buffer.readLine()) != null){
            line = line.replaceAll("á", "a");
            line = line.replaceAll("é", "e");
            line = line.replaceAll("í", "i");
            line = line.replaceAll("ó", "o");
            line = line.replaceAll("ú", "u");
            line = line.replaceAll("Á", "A");
            line = line.replaceAll("É", "E");
            line = line.replaceAll("Í", "I");
            line = line.replaceAll("Ó", "O");
            line = line.replaceAll("Ú", "U");
            System.out.println(line);
            writer.write(line);
            writer.write("\n");
        }
        reader.close();
        writer.close();
    }

    public static void dieciocho() throws IOException {
        System.out.print("18. Reemplaza, del resultado obtenido en el ejercicio anterior, las palabras formadas únicamente por números por un espacio.\n");
        File file = new File("ejercicio17.txt");
        FileReader reader = new FileReader(file);
        BufferedReader buffer = new BufferedReader(reader);
        FileWriter writer = new FileWriter("ejercicio18.txt");
        String line = null;
        Pattern patron = Pattern.compile(" [0-9]* ");
        while((line = buffer.readLine()) != null){
            Matcher mat = patron.matcher(line);
            line = mat.replaceAll(" ");
            System.out.println(line);
            writer.write(line);
            writer.write("\n");
        }
        reader.close();
        writer.close();
    }

    public static void diecinueve() throws IOException {
        System.out.println("19. Convierte el texto anterior a mayúsculas.");
        File file = new File("ejercicio18.txt");
        FileReader reader = new FileReader(file);
        BufferedReader buffer = new BufferedReader(reader);
        FileWriter writer = new FileWriter("ejercicio19.txt");
        String line = null;
        Pattern patron = Pattern.compile("[0-9]*");
        while((line = buffer.readLine()) != null){
            line = line.toUpperCase();
            System.out.println(line);
            writer.write(line);
            writer.write("\n");
        }
        reader.close();
        writer.close();
    }

    public static void veinte() throws IOException {
        System.out.print("20. Reemplaza los dobles espacios que se hayan podido crear por un único espacio.\n");
        File file = new File("ejercicio19.txt");
        FileReader reader = new FileReader(file);
        BufferedReader buffer = new BufferedReader(reader);
        FileWriter writer = new FileWriter("ejercicio20.txt");
        String line = null;
        Pattern patron = Pattern.compile("  ");
        while((line = buffer.readLine()) != null){
            Matcher mat = patron.matcher(line);
            line = mat.replaceAll(" ");
            System.out.println(line);
            writer.write(line);
            writer.write("\n");
        }
        reader.close();
        writer.close();
    }
}
