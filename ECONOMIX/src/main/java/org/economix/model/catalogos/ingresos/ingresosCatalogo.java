// Paquete que contiene los catálogos relacionados con ingresos (versión consola).
package org.economix.model.catalogos.ingresos;

// Importación de clases necesarias: entidades, repositorios y utilidades.
import org.economix.model.ingresos.Ingresos;
import org.economix.sql.hibernateimpl.ingresos.IngresosHiberImpl;
import org.economix.model.usuario.Usuario;
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.usuario.UsuarioHiberImpl;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

import java.sql.Date;

/**
 * Catálogo para la gestión de ingresos en consola.
 * Permite registrar, editar y vincular ingresos con usuarios utilizando Hibernate.
 * Extiende {@link gestorCatalogos} y aplica el patrón Singleton.
 */
public class ingresosCatalogo extends gestorCatalogos<Ingresos> {

    /** Instancia singleton del catálogo */
    private static ingresosCatalogo ingresosCatalogo;

    /** Repositorio para acceder a la tabla de ingresos */
    private static final GenericSql<Ingresos> ingresosHiber = IngresosHiberImpl.getInstance();

    /** Repositorio para acceder a la tabla de usuarios */
    private static final GenericSql<Usuario> usuarioHiber = UsuarioHiberImpl.getInstance();

    /**
     * Obtiene la instancia única del catálogo.
     * regresa objeto singleton de {@code ingresosCatalogo}.
     */
    public static ingresosCatalogo getInstance() {
        if (ingresosCatalogo == null) {
            ingresosCatalogo = new ingresosCatalogo();
        }
        return ingresosCatalogo;
    }

    /**
     * Constructor privado que configura el repositorio base.
     */
    private ingresosCatalogo() {
        super(IngresosHiberImpl.getInstance());
    }

    /**
     * Crea una nueva instancia vacía de {@link Ingresos}.
     * regresa nuevo objeto de ingreso.
     */
    @Override
    public Ingresos newT() {
        return new Ingresos();
    }

    /**
     * Procesa la creación de un nuevo ingreso desde consola.
     * Solicita al usuario los datos requeridos y vincula el ingreso a un usuario existente.
     * @param ingresos objeto a llenar y guardar.
     * regresa true si se guarda correctamente, false si falla la validación.
     */
    @Override
    public boolean processNewT(Ingresos ingresos) {
        System.out.print("> Teclee el monto del ingreso: ");
        ingresos.setMontoIngreso(readUtil.readBigDecimal());

        System.out.print("> Periodicidad: ");
        ingresos.setPeriodicidadIngreso(readUtil.read());

        System.out.print("> Fecha (yyyy-MM-dd): ");
        ingresos.setFechaIngresos(Date.valueOf(readUtil.read()));

        System.out.print("> Descripción: ");
        ingresos.setDescripcionIngreso(readUtil.read());

        // Asociación con usuario existente
        System.out.print("> ID del usuario que registra el gasto: ");
        Integer idUsuario = readUtil.readInt();
        Usuario usuario = usuarioHiber.findById(idUsuario);
        if (usuario == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó el gasto.");
            return false;
        }
        ingresos.setUsuario(usuario);

        // Guardar en base de datos
        ingresosHiber.save(ingresos);
        return true;
    }

    /**
     * Procesa la edición de un ingreso existente.
     * Permite modificar los datos del ingreso y cambiar el usuario asociado si se desea.
     * @param ingresos objeto a modificar.
     * devuelve true si se edita y guarda correctamente.
     */
    @Override
    public boolean processEditT(Ingresos ingresos) {
        System.out.print("> Teclee el nuevo monto del ingreso: ");
        ingresos.setMontoIngreso(readUtil.readBigDecimal());

        System.out.print("> Periodicidad Nueva: ");
        ingresos.setPeriodicidadIngreso(readUtil.read());

        System.out.print("> Fecha Nueva (yyyy-MM-dd): ");
        ingresos.setFechaIngresos(Date.valueOf(readUtil.read()));

        System.out.print("> Descripción Nueva: ");
        ingresos.setDescripcionIngreso(readUtil.read());

        // Posible cambio de usuario
        System.out.print("> ID de usuario (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Usuario u = usuarioHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Usuario no existe; se mantiene el actual.");
            } else {
                ingresos.setUsuario(u);
            }
        }

        ingresosHiber.update(ingresos);
        return true;
    }
}
