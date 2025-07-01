// Paquete que contiene los menús del sistema relacionados a ingresos
package org.economix.vista.listas;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;
import org.economix.model.catalogos.ingresos.ingresosCatalogo;
import org.economix.model.catalogos.ingresos.conceptoIngresosCatalogo;

/**
 * Submenú del sistema para gestionar catálogos relacionados a ingresos.
 * Permite acceder a ingresos registrados, conceptos de ingreso, y presupuesto (futuro).
 * Utiliza el patrón Singleton para garantizar una sola instancia.
 */
public class listaIngresos extends leerAcciones {

    /** Instancia única del menú de ingresos (Singleton) */
    private static listaIngresos listaIngresos;

    /** Constructor privado para el patrón Singleton */
    private listaIngresos() {}

    /**
     * Devuelve la instancia única de listaIngresos.
     * devuelve instancia de listaIngresos
     */
    public static listaIngresos getInstance() {
        if (listaIngresos == null) {
            listaIngresos = new listaIngresos();
        }
        return listaIngresos;
    }

    /**
     * Muestra las opciones disponibles en el submenú de ingresos.
     */
    @Override
    public void despliegaMenu() {
        System.out.println("\n\t::: Catálogo de Usuario :::");
        System.out.println("1.- Ingresos");
        System.out.println("2.- Concepto Ingresos");
        System.out.println("3.- Presupuesto");
        System.out.println("4.- Salir ");
        Menu.seleccionaOpcion();
    }

    /**
     * Valor mínimo permitido para la opción seleccionada.
     * devuelve 1
     */
    @Override
    public int valorMinMenu() {
        return 1;
    }

    /**
     * Valor máximo permitido para la opción seleccionada.
     * devuelve 4
     */
    @Override
    public int valorMaxMenu() {
        return 4;
    }

    /**
     * Ejecuta la opción seleccionada por el usuario.
     */
    @Override
    public void procesaOpcion() {
        Ejecutable ejecutable = null;

        switch (opcion) {
            case 1:
                ejecutable = ingresosCatalogo.getInstance();              // Gestión de ingresos
                break;
            case 2:
                ejecutable = conceptoIngresosCatalogo.getInstance();      // Gestión de conceptos de ingreso
                break;
            case 3:
                // Opción aún no implementada (presupuestos)
                System.out.println("> Módulo de presupuesto no implementado.");
                break;
            case 4:
                flag = false; // Salir del menú
                break;
            default:
                Menu.opcionInvalida(); // Opción inválida
                break;
        }

        // Si se seleccionó una opción válida con lógica definida, ejecutarla
        if (ejecutable != null) {
            ejecutable.setFlag(true);
            ejecutable.run();
        }
    }
}
