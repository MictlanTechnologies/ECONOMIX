// Paquete que contiene acciones comunes reutilizables para catálogos
package org.economix.vista.acciones;

import org.economix.model.Catalogo;
import org.economix.sql.GenericSql;
import org.economix.util.readUtil;

import java.util.List;

/**
 * Clase abstracta genérica para gestionar catálogos (CRUD) en consola.
 * Utiliza programación genérica y herencia para manejar entidades tipo Catalogo.
 *
 * @param <T> Tipo de entidad que extiende de Catalogo (con atributo ID).
 */
public abstract class gestorCatalogos<T extends Catalogo> extends leerAcciones {

    // Lista de todos los elementos del catálogo
    protected List<T> list;

    // Elemento actual que se está manipulando
    protected T t;

    // Bandera de control para loops interactivos
    protected boolean flag2;

    // DAO genérico para operaciones Hibernate
    protected GenericSql<T> genericSql;

    /**
     * Constructor que recibe el DAO específico para la entidad.
     * @param genericSql implementación de acceso a datos
     */
    public gestorCatalogos(GenericSql<T> genericSql) {
        this.genericSql = genericSql;
    }

    // Métodos abstractos que deben ser implementados por cada subclase
    public abstract T newT();                         // Crea un nuevo objeto del tipo
    public abstract boolean processNewT(T t);         // Procesa el guardado del nuevo objeto
    public abstract boolean processEditT(T t);        // Procesa la edición del objeto

    /**
     * Imprime todos los elementos del catálogo.
     */
    public void print() {
        List<T> list = genericSql.findAll();
        if (list.isEmpty()) {
            System.out.println("> No hay elementos registrados.");
        }
        list.forEach(System.out::println);
    }

    /**
     * Agrega un nuevo elemento al catálogo.
     */
    public void add() {
        t = newT();
        if (processNewT(t)) {
            System.out.println("> Elemento añadido con éxito.");
        }
    }

    /**
     * Permite editar un elemento existente mediante su ID.
     */
    public void edit() {
        List<T> list = genericSql.findAll();
        if (list.isEmpty()) {
            System.out.println("> No hay elementos para editar.");
            return;
        }

        flag2 = true;
        while (flag2) {
            list.forEach(System.out::println);
            System.out.print("> Ingrese el ID del elemento a editar: ");

            t = list.stream()
                    .filter(e -> e.getId().equals(readUtil.readInt()))
                    .findFirst()
                    .orElse(null);

            if (t == null) {
                System.out.println("> No se encontró el elemento.");
                System.out.print("> Deseas volver a intentarlo? s/n: ");
                String respuesta = readUtil.read();
                flag2 = respuesta.equalsIgnoreCase("S");
            } else {
                if (processEditT(t)) {
                    System.out.println("> Elemento editado con éxito.");
                }
                flag2 = false;
            }
        }
    }

    /**
     * Elimina un elemento del catálogo por su ID.
     */
    public void remove() {
        List<T> list = genericSql.findAll();
        if (list == null || list.isEmpty()) {
            System.out.println("> No hay elementos para eliminar.");
            return;
        }

        flag2 = true;
        while (flag2) {
            list.forEach(System.out::println);
            System.out.print("> Ingrese el ID del elemento a eliminar: ");

            t = list.stream()
                    .filter(e -> e.getId().equals(readUtil.readInt()))
                    .findFirst()
                    .orElse(null);

            if (t == null) {
                System.out.println("> No se encontró el elemento.");
                System.out.print("> Deseas volver a intentarlo? s/n: ");
                String respuesta = readUtil.read();
                flag2 = respuesta.equalsIgnoreCase("S");
            } else {
                if (genericSql.delete(t)) {
                    System.out.println("> Elemento eliminado con éxito.");
                }
                flag2 = false;
            }
        }
    }

    /**
     * Busca e imprime un elemento del catálogo por su ID.
     */
    public void findById() {
        System.out.print("> Ingresa un ID para buscar: ");
        t = genericSql.findById(readUtil.readInt());

        if (t != null) {
            System.out.println(t);
        } else {
            System.out.println("> No existe un elemento con dicho ID.");
        }
    }

    /**
     * Despliega el menú interactivo en consola.
     */
    @Override
    public void despliegaMenu() {
        System.out.println("\n\t:: Gestión de Gastos ::");
        System.out.println("Seleccione una opción:");
        System.out.println("1.- Agregar");
        System.out.println("2.- Eliminar");
        System.out.println("3.- Editar");
        System.out.println("4.- Imprimir elementos en lista");
        System.out.println("5.- Obtener por su ID");
        System.out.println("6.- Salir");
        Menu.seleccionaOpcion();
    }

    @Override
    public int valorMinMenu() {
        return 1;
    }

    @Override
    public int valorMaxMenu() {
        return 6;
    }

    /**
     * Procesa la opción elegida del menú.
     */
    @Override
    public void procesaOpcion() {
        switch (opcion) {
            case 1 -> add();
            case 2 -> remove();
            case 3 -> edit();
            case 4 -> print();
            case 5 -> findById();
            default -> Menu.opcionInvalida();
        }
    }
}
