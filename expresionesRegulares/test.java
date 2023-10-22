import java.io.IOException;
import java.util.Scanner;

public class test {
    public interface funcion{
        void llamar() throws IOException;
    }

    public static class bucle{
        public static void ejecutar(funcion f) throws IOException {
            Scanner respuesta = new Scanner(System.in);
            do{
                System.out.println();
                f.llamar();
                System.out.print("\nPulse enter si desea continuar al siguiente ejercicio. De lo contrario, escriba cualquier cosa: ");
            }while(!respuesta.nextLine().isEmpty());
        }
    }
    public static void main(String[] args) throws IOException {
        bucle b = new bucle();
        System.out.println("-------------------------------------------------------- PROGRAMA DE PRUEBA PARA EXPRESIONES REGULARES --------------------------------------------------------");
        bucle.ejecutar(ejercicios::uno);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::dos);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::tres);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::cuatro);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::cinco);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::seis);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::siete);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::ocho);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::nueve);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::diez);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::once);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::doce);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::trece);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::catorce);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::quince);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::dieciseis);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::diecisiete);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::dieciocho);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::diecinueve);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        bucle.ejecutar(ejercicios::veinte);
        System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }
}
