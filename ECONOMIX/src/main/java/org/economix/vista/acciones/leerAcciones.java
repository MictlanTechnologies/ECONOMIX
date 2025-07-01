// Paquete con clases base para menús interactivos de consola
package org.economix.vista.acciones;

import org.economix.util.readUtil;

/**
 * Clase abstracta que representa un ciclo de menú en consola.
 * Define la lógica básica para mostrar opciones y procesar acciones del usuario.
 * Las subclases deben implementar los métodos para mostrar el menú,
 * validar los límites y ejecutar las acciones correspondientes.
 */
public abstract class leerAcciones implements Ejecutable {

    // Opción seleccionada por el usuario
    protected Integer opcion;

    // Bandera de control para el ciclo principal
    protected boolean flag;

    /**
     * Constructor que inicializa la bandera en true
     * para mantener activo el ciclo del menú.
     */
    public leerAcciones() {
        flag = true;
    }

    /**
     * Método abstracto que debe mostrar el menú en consola.
     */
    public abstract void despliegaMenu();

    /**
     * Valor mínimo permitido en el menú (usualmente 1).
     * devuelve valor mínimo aceptado
     */
    public abstract int valorMinMenu();

    /**
     * Valor máximo permitido en el menú (normalmente representa la opción de salir).
     * devuelve valor máximo aceptado
     */
    public abstract int valorMaxMenu();

    /**
     * Ejecuta la acción correspondiente a la opción seleccionada.
     */
    public abstract void procesaOpcion();

    /**
     * Método que ejecuta el ciclo completo del menú,
     * solicitando al usuario una opción y ejecutando la acción asociada.
     */
    @Override
    public void run() {
        while (flag) {
            despliegaMenu();                   // Mostrar menú en pantalla
            opcion = readUtil.readInt();       // Leer opción del usuario

            if (opcion >= valorMinMenu() && opcion <= valorMaxMenu()) {
                if (opcion == valorMaxMenu()) {
                    flag = false;              // Salir del ciclo
                } else {
                    procesaOpcion();           // Ejecutar acción
                }
            } else {
                Menu.opcionInvalida();         // Mensaje de opción inválida
            }
        }
    }

    /**
     * Permite activar o desactivar manualmente el ciclo del menú.
     * @param flag true para continuar, false para salir.
     */
    @Override
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
