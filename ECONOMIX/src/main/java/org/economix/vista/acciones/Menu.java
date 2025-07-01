// Paquete que contiene utilidades para interacción en consola
package org.economix.vista.acciones;

/**
 * Clase utilitaria que muestra mensajes comunes
 * relacionados con el manejo de menús en consola.
 */
public class Menu {

    /**
     * Muestra un mensaje de error cuando se selecciona una opción no válida del menú.
     */
    public static void opcionInvalida() {
        System.out.println("\t¡ERROR! ¡La opción no es correcta!");
    }

    /**
     * Muestra el mensaje que solicita al usuario ingresar una opción del menú.
     */
    public static void seleccionaOpcion() {
        System.out.print("> Dame una opción: ");
    }

    /**
     * Muestra un mensaje de error cuando el usuario ingresa un dato inválido (no numérico, etc.).
     */
    public static void errorDato() {
        System.out.println("\t¡ERROR! ¡Ese no es un dato válido!");
    }
}
