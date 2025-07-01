// Paquete que contiene la ejecución principal en modo consola
package org.economix.vista.consola;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;
import org.economix.vista.listas.listaCatalogos;

/**
 * Clase que representa el menú principal en modo consola para ECONOMIX.
 * Permite al usuario acceder a los submenús disponibles.
 * Implementa patrón Singleton.
 */
public class Consola extends leerAcciones {

    // Instancia única del menú consola
    private static Consola consola;

    /** Constructor privado para patrón Singleton */
    private Consola() {
    }

    /**
     * Devuelve la única instancia de Consola (Singleton).
     * devuelve instancia de Consola
     */
    public static Consola getInstance() {
        if (consola == null) {
            consola = new Consola();
        }
        return consola;
    }

    /**
     * Muestra las opciones disponibles del menú principal en consola.
     */
    @Override
    public void despliegaMenu() {
        System.out.println("\n\t::: Menú principal :::");
        System.out.println("\t> Selecciona una opción:");
        System.out.println("1. Menu Economix");
        System.out.println("2. Menu Ventana Economix");
        System.out.println("3. Salir");
        Menu.seleccionaOpcion();
    }

    /**
     * Límite inferior de opciones permitidas.
     * devuelve 1
     */
    @Override
    public int valorMinMenu() {
        return 1;
    }

    /**
     * Límite superior de opciones permitidas.
     * devuelve 3
     */
    @Override
    public int valorMaxMenu() {
        return 3;
    }

    /**
     * Ejecuta la acción correspondiente a la opción seleccionada.
     */
    @Override
    public void procesaOpcion() {
        Ejecutable ejecutable = null;

        if (opcion == 1) {
            // Ejecuta el menú de catálogos ECONOMIX (gastos, ingresos, etc.)
            ejecutable = listaCatalogos.getInstance();
            ejecutable.setFlag(true);
            ejecutable.run();
        }

        if (opcion == 2) {
            // Placeholder: aún no implementada la interfaz gráfica desde consola
            System.out.println("> No implementado.");
        }
    }
}
