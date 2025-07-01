// Paquete que contiene el menú de funciones operativas (ingresos, gastos, etc.)
package org.economix.vista.listas;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;

/**
 * Menú de selección de funciones principales del sistema ECONOMIX.
 * Desde aquí se accede a la gestión de ingresos y gastos.
 * Implementa el patrón Singleton para asegurar una única instancia.
 */
public class listaFunciones extends leerAcciones {

    // Instancia única de la clase
    private static listaFunciones listaFunciones;

    /** Constructor privado (Singleton) */
    private listaFunciones() {
    }

    /**
     * Devuelve la instancia única del menú de funciones.
     * regresa instancia única de listaFunciones
     */
    public static listaFunciones getInstance() {
        if (listaFunciones == null) {
            listaFunciones = new listaFunciones();
        }
        return listaFunciones;
    }

    /**
     * Muestra el menú de funciones disponibles.
     */
    @Override
    public void despliegaMenu() {
        System.out.println("\n\t::: Catálogo de Funciones :::");
        System.out.println("1.- Ingresos");
        System.out.println("2.- Gastos");
        System.out.println("3.- Salir");
        Menu.seleccionaOpcion();
    }

    /**
     * Valor mínimo permitido para la opción del menú.
     * devuelve 1
     */
    @Override
    public int valorMinMenu() {
        return 1;
    }

    /**
     * Valor máximo permitido para la opción del menú.
     * regresa 3
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

        switch (opcion) {
            case 1:
                ejecutable = listaIngresos.getInstance();  // Submenú de ingresos
                break;
            case 2:
                ejecutable = listaGastos.getInstance();    // Submenú de gastos
                break;
            case 3:
                flag = false; // Terminar ejecución del menú actual
                break;
            default:
                Menu.opcionInvalida(); // En caso de opción incorrecta
                break;
        }

        // Ejecuta el submenú correspondiente si existe
        if (ejecutable != null) {
            ejecutable.setFlag(true);
            ejecutable.run();
        }
    }
}
