// Paquete principal que contiene la clase lanzadora de la aplicación por consola.
package org.economix.Inicio;

// Importa el manejador de selección de funciones a ejecutar en la app.
import org.economix.vista.acciones.seleccionEjecutable;

/**
 * Clase principal que inicia la ejecución del sistema ECONOMIX en modo consola.
 * Muestra un mensaje de bienvenida, ejecuta la interfaz de selección y finaliza con un mensaje de despedida.
 */
public class Inicio {

    /**
     * Método principal de la aplicación. Punto de entrada del programa.
     * Llama al singleton de la clase encargada de ejecutar las acciones seleccionadas por el usuario.
     * Muestra también mensajes decorativos al inicio y fin de la sesión.
     */
    public static void main(String[] args) {
        System.out.println("\t:: ECONOMIX ::"); // Mensaje de bienvenida
        seleccionEjecutable.getInstance().run(); // Inicia la lógica de selección de acción
        System.out.println("\t¡Hasta pronto!");  // Mensaje de salida
    }
}
