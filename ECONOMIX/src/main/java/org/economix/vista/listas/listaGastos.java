// Paquete que contiene el menú de selección de catálogos relacionados a gastos
package org.economix.vista.listas;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;
import org.economix.model.catalogos.gastos.conceptoGastosCatalogo;
import org.economix.model.catalogos.gastos.gastosCatalogo;

/**
 * Menú que agrupa las funciones de gestión de gastos y conceptos de gasto.
 * Permite al usuario acceder a los catálogos relacionados con el módulo de gastos.
 * Implementa Singleton para evitar múltiples instancias.
 */
public class listaGastos extends leerAcciones {

    /** Instancia única del menú de gastos (Singleton) */
    private static listaGastos listaGastos;

    /** Constructor privado */
    private listaGastos() {}

    /**
     * Devuelve la instancia única del menú de gastos.
     * devuelve instancia de listaGastos
     */
    public static listaGastos getInstance() {
        if (listaGastos == null) {
            listaGastos = new listaGastos();
        }
        return listaGastos;
    }

    /**
     * Muestra las opciones disponibles del submenú de gastos.
     */
    @Override
    public void despliegaMenu() {
        System.out.println("\n\t::: Catálogo de Usuario :::");
        System.out.println("1.- Gastos");
        System.out.println("2.- Conceptos Gastos");
        System.out.println("3.- Salir ");
        Menu.seleccionaOpcion();
    }

    /**
     * Valor mínimo permitido en el menú.
     * devuelve 1
     */
    @Override
    public int valorMinMenu() {
        return 1;
    }

    /**
     * Valor máximo permitido en el menú.
     * devuelve 3
     */
    @Override
    public int valorMaxMenu() {
        return 3;
    }

    /**
     * Ejecuta la opción seleccionada por el usuario.
     */
    @Override
    public void procesaOpcion() {
        Ejecutable ejecutable = null;

        switch (opcion) {
            case 1:
                ejecutable = gastosCatalogo.getInstance();           // Accede a gestión de gastos
                break;
            case 2:
                ejecutable = conceptoGastosCatalogo.getInstance();   // Accede a gestión de conceptos
                break;
            case 3:
                flag = false; // Salir del menú
                break;
            default:
                Menu.opcionInvalida(); // Si el número no es válido
                break;
        }

        // Ejecuta el submenú seleccionado, si aplica
        if (ejecutable != null) {
            ejecutable.setFlag(true);
            ejecutable.run();
        }
    }
}
