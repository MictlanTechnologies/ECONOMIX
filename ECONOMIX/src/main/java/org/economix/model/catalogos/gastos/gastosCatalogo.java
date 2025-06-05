package org.economix.model.catalogos.gastos;

import org.economix.model.gastos.Gastos;
import org.economix.model.usuario.Usuario;                 // ← importa la entidad
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.gastos.GastosHiberImpl;
import org.economix.sql.hibernateimpl.usuario.UsuarioHiberImpl; // ← importa la impl
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

import java.sql.Date;

public class gastosCatalogo extends gestorCatalogos<Gastos> {

    private static gastosCatalogo gastosCatalogo;

    /** repositorios */
    private static final GenericSql<Gastos>   gastosHiber   = GastosHiberImpl.getInstance();
    private static final GenericSql<Usuario>  usuarioHiber  = UsuarioHiberImpl.getInstance(); // ★ NUEVO

    /** singleton */
    public static gastosCatalogo getInstance() {
        if (gastosCatalogo == null) {
            gastosCatalogo = new gastosCatalogo();
        }
        return gastosCatalogo;
    }

    private gastosCatalogo() {
        super(GastosHiberImpl.getInstance());
    }

    @Override
    public Gastos newT() {
        return new Gastos();
    }

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

        /* …todo lo que ya tenías… */
        System.out.print("> Periodo: ");
        gastos.setPeriodoGastos(readUtil.read());

        /* ==== VINCULACIÓN AL USUARIO ==== */
        System.out.print("> ID del usuario que registra el gasto: ");
        Integer idUsuario = readUtil.readInt();

        Usuario usuario = usuarioHiber.findById(idUsuario);
        if (usuario == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó el gasto.");
            return false;
        }
        gastos.setUsuario(usuario);        // ★ clave

        /* === guarda === */
        gastosHiber.save(gastos);
        return true;
    }

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