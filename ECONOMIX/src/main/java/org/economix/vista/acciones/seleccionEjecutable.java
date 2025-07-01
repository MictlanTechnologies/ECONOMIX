// Paquete que contiene la lógica de selección de interfaz
package org.economix.vista.acciones;

import org.economix.vista.consola.Consola;

/**
 * Clase encargada de mostrar el menú inicial para elegir
 * el tipo de interfaz (consola o ventana) en ECONOMIX.
 * Implementa el patrón Singleton.
 */
public class seleccionEjecutable extends leerAcciones {

    // Instancia única (singleton)
    public static seleccionEjecutable seleccionEjecutable;

    /** Constructor privado para evitar instancias múltiples */
    private seleccionEjecutable() {
    }

    /**
     * Devuelve la instancia única del selector de ejecución.
     * devuelve instancia de seleccionEjecutable
     */
    public static seleccionEjecutable getInstance() {
        if (seleccionEjecutable == null) {
            seleccionEjecutable = new seleccionEjecutable();
        }
        return seleccionEjecutable;
    }

    /**
     * Muestra el menú principal para elegir entre consola o ventana.
     */
    @Override
    public void despliegaMenu() {
        System.out.println("\n\t::: ¡Bienvenido a ECONOMIX! :::");
        System.out.println("\t> Selecciona tu método de acceso:");
        System.out.println("1. Consola");
        System.out.println("2. Ventana");
        System.out.println("3. Salir");
        Menu.seleccionaOpcion();
    }

    /**
     * Límite inferior del menú.
     * devuelve 1
     */
    @Override
    public int valorMinMenu() {
        return 1;
    }

    /**
     * Límite superior del menú.
     * devuelve 3
     */
    @Override
    public int valorMaxMenu() {
        return 3;
    }

    /**
     * Procesa la opción seleccionada por el usuario.
     */
    @Override
    public void procesaOpcion() {
        Ejecutable ejecutable = null;

        if (opcion == 1) {
            ejecutable = Consola.getInstance();  // Lanza la versión por consola
        }

        if (opcion == 2) {
            ejecutable = null; // Aquí se conectaría la interfaz de ventana
        }

        if (ejecutable != null) {
            ejecutable.setFlag(true);
            ejecutable.run();
        }
    }
}
