// Paquete que contiene los catálogos relacionados con gastos (versión consola).
package org.economix.model.catalogos.gastos;

// Importación de clases necesarias: entidades, repositorios y utilidades.
import org.economix.model.gastos.Gastos;
import org.economix.model.usuario.Usuario;
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.gastos.GastosHiberImpl;
import org.economix.sql.hibernateimpl.usuario.UsuarioHiberImpl;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

import java.sql.Date;

/**
 * Catálogo para la gestión de gastos en consola.
 * Permite registrar, editar y vincular gastos con usuarios utilizando Hibernate.
 * Extiende {@link gestorCatalogos} y aplica patrón singleton.
 */
public class gastosCatalogo extends gestorCatalogos<Gastos> {

    /** Instancia singleton del catálogo */
    private static gastosCatalogo gastosCatalogo;

    /** Repositorio para acceder a la tabla de gastos */
    private static final GenericSql<Gastos> gastosHiber = GastosHiberImpl.getInstance();

    /** Repositorio para acceder a la tabla de usuarios */
    private static final GenericSql<Usuario> usuarioHiber = UsuarioHiberImpl.getInstance();

    /**
     * Obtiene la instancia única del catálogo.
     * @return objeto singleton de {@code gastosCatalogo}.
     */
    public static gastosCatalogo getInstance() {
        if (gastosCatalogo == null) {
            gastosCatalogo = new gastosCatalogo();
        }
        return gastosCatalogo;
    }

    /**
     * Constructor privado que configura el repositorio base.
     */
    private gastosCatalogo() {
        super(GastosHiberImpl.getInstance());
    }

    /**
     * Crea una nueva instancia vacía de {@link Gastos}.
     * @return nuevo objeto de gastos.
     */
    @Override
    public Gastos newT() {
        return new Gastos();
    }

    /**
     * Procesa la creación de un nuevo gasto desde consola.
     * Pide todos los datos, verifica existencia del usuario, y guarda.
     * @param gastos objeto de gasto a llenar.
     * @return true si se guarda correctamente, false si falla la validación.
     */
    @Override
    public boolean processNewT(Gastos gastos) {
        System.out.print("> Teclee el nombre del artículo: ");
        gastos.setArticuloGasto(readUtil.read());

        System.out.print("> Descripción: ");
        gastos.setDescripcionGastos(readUtil.read());

        System.out.print("> Monto: ");
        gastos.setMontoGastos(readUtil.readBigDecimal());

        System.out.print("> Fecha (yyyy-MM-dd): ");
        gastos.setFechaGastos(Date.valueOf(readUtil.read()));

        System.out.print("> Periodo: ");
        gastos.setPeriodoGastos(readUtil.read());

        // Asociación con usuario existente
        System.out.print("> ID del usuario que registra el gasto: ");
        Integer idUsuario = readUtil.readInt();
        Usuario usuario = usuarioHiber.findById(idUsuario);
        if (usuario == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó el gasto.");
            return false;
        }
        gastos.setUsuario(usuario);

        // Guardar en base de datos
        gastosHiber.save(gastos);
        return true;
    }

    /**
     * Procesa la edición de un gasto existente.
     * Permite cambiar todos los campos, incluyendo el usuario si se desea.
     * @param gastos objeto de gasto a modificar.
     * devuelve true si se edita correctamente.
     */
    @Override
    public boolean processEditT(Gastos gastos) {
        System.out.print("> Teclee el nuevo nombre del artículo: ");
        gastos.setArticuloGasto(readUtil.read());

        System.out.print("> Nueva Descripción: ");
        gastos.setDescripcionGastos(readUtil.read());

        System.out.print("> Nuevo Monto: ");
        gastos.setMontoGastos(readUtil.readBigDecimal());

        System.out.print("> Nueva Fecha (yyyy-MM-dd): ");
        gastos.setFechaGastos(Date.valueOf(readUtil.read()));

        System.out.print("> Nuevo Periodo: ");
        gastos.setPeriodoGastos(readUtil.read());

        System.out.print("> ID de usuario (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Usuario u = usuarioHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Usuario no existe; se mantiene el actual.");
            } else {
                gastos.setUsuario(u);
            }
        }

        gastosHiber.update(gastos);
        return true;
    }
}
