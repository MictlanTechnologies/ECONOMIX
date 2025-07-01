// Paquete del sistema que contiene los menús relacionados a la gestión de usuarios
package org.economix.vista.listas;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;
import org.economix.model.catalogos.usuario.domicilioCatalogo;
import org.economix.model.catalogos.usuario.personaCatalogo;
import org.economix.model.catalogos.usuario.usuarioCatalogo;
import org.economix.model.catalogos.usuario.contactoCatalogo;

/**
 * Submenú del sistema para la gestión de entidades relacionadas al usuario:
 * Usuario, Persona, Domicilio y Contacto.
 * Implementa el patrón Singleton para mantener una única instancia del menú.
 */
public class listaUsuario extends leerAcciones {

    /** Instancia única de listaUsuario (Singleton) */
    private static listaUsuario listaUsuario;

    /** Constructor privado para restringir instanciación externa */
    private listaUsuario() {}

    /**
     * Devuelve la instancia única de listaUsuario.
     * regresa instancia única de listaUsuario
     */
    public static listaUsuario getInstance() {
        if (listaUsuario == null) {
            listaUsuario = new listaUsuario();
        }
        return listaUsuario;
    }

    /**
     * Despliega el submenú de opciones relacionadas con entidades de usuario.
     */
    @Override
    public void despliegaMenu() {
        System.out.println("\n\t::: Catálogo de Usuario :::");
        System.out.println("1.- Usuario");
        System.out.println("2.- Persona");
        System.out.println("3.- Domicilio");
        System.out.println("4.- Contacto");
        System.out.println("5.- Salir ");
        Menu.seleccionaOpcion();
    }

    /**
     * Valor mínimo del menú (usado para validación de entrada).
     * regresa 1
     */
    @Override
    public int valorMinMenu() {
        return 1;
    }

    /**
     * Valor máximo del menú (usado para validación de entrada).
     * devuelve 5
     */
    @Override
    public int valorMaxMenu() {
        return 5;
    }

    /**
     * Procesa la opción seleccionada por el usuario y redirige
     * al catálogo correspondiente (Usuario, Persona, Domicilio, Contacto).
     */
    @Override
    public void procesaOpcion() {
        Ejecutable ejecutable = null;

        switch (opcion) {
            case 1:
                ejecutable = usuarioCatalogo.getInstance();   // Gestión de Usuario
                break;
            case 2:
                ejecutable = personaCatalogo.getInstance();   // Gestión de Persona
                break;
            case 3:
                ejecutable = domicilioCatalogo.getInstance(); // Gestión de Domicilio
                break;
            case 4:
                ejecutable = contactoCatalogo.getInstance();  // Gestión de Contacto
                break;
            case 5:
                flag = false; // Salir del menú
                break;
            default:
                Menu.opcionInvalida(); // Entrada inválida
                break;
        }

        // Ejecutar el módulo correspondiente si fue seleccionado
        if (ejecutable != null) {
            ejecutable.setFlag(true);
            ejecutable.run();
        }
    }
}
