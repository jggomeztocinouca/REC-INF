import java.io.IOException;
import java.util.Scanner;

public class test {
    public static void limpiarPantalla(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
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
    
    public static void main(String[] args) throws IOException { // BUSCAR LA MANERA DE SIMPLIFICAR
        bucle b = new bucle();
        System.out.println("-------------------- PROGRAMA DE PRUEBA PARA EXPRESIONES REGULARES --------------------");
        bucle.ejecutar(ejercicios::uno);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::dos);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::tres);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::cuatro);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::cinco);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::seis);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::siete);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::ocho);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::nueve);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::diez);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::once);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::doce);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::trece);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::catorce);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::quince);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::dieciseis);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::diecisiete);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::dieciocho);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::diecinueve);
        limpiarPantalla();
        bucle.ejecutar(ejercicios::veinte);
        limpiarPantalla();
    }
}
