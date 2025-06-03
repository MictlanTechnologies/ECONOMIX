package org.economix.vista.catalogos.ingresos;

import org.economix.Ingresos.Ingresos;
import org.economix.sql.hibernateimpl.ingresos.IngresosHiberImpl;
import org.economix.usuario.Usuario;                 // ← importa la entidad
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.usuario.UsuarioHiberImpl; // ← importa la impl
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

import java.sql.Date;

public class ingresosCatalogo extends gestorCatalogos<Ingresos> {

    private static ingresosCatalogo ingresosCatalogo;

    /** repositorios */
    private static final GenericSql<Ingresos>   ingresosHiber  = IngresosHiberImpl.getInstance();
    private static final GenericSql<Usuario>  usuarioHiber  = UsuarioHiberImpl.getInstance(); // ★ NUEVO

    /** singleton */
    public static ingresosCatalogo getInstance() {
        if (ingresosCatalogo == null) {
            ingresosCatalogo = new ingresosCatalogo();
        }
        return ingresosCatalogo;
    }

    private ingresosCatalogo() {
        super(IngresosHiberImpl.getInstance());
    }

    @Override
    public Ingresos newT() {
        return new Ingresos();
    }

    @Override
    public boolean processNewT(Ingresos ingresos) {
        System.out.print("> Teclee el nombre del artículo: ");
        ingresos.setMontoIngreso(readUtil.readBigDecimal());

        System.out.print("> Descripción: ");
        ingresos.setPreriodicidadIngresos(readUtil.read());

        System.out.print("> Monto: ");
        ingresos.setFechaIngresos(Date.valueOf(readUtil.read()));

        System.out.print("> Fecha (yyyy-MM-dd): ");
        ingresos.setDescripcionIngreso(readUtil.read()  );

        /* ==== VINCULACIÓN AL USUARIO ==== */
        System.out.print("> ID del usuario que registra el gasto: ");
        Integer idUsuario = readUtil.readInt();

        Usuario usuario = usuarioHiber.findById(idUsuario);
        if (usuario == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó el gasto.");
            return false;
        }
        ingresos.setUsuario(usuario);        // ★ clave

        /* === guarda === */
        ingresosHiber.save(ingresos);
        return true;
    }

    @Override
    public boolean processEditT(Ingresos ingresos) {
        System.out.print("> Teclee el nuevo nombre del artículo: ");
        ingresos.setMontoIngreso(readUtil.readBigDecimal());

        System.out.print("> Descripción : ");
        ingresos.setPreriodicidadIngresos(readUtil.read());

        System.out.print("> Monto: ");
        ingresos.setFechaIngresos(Date.valueOf(readUtil.read()));

        System.out.print("> Fecha (yyyy-MM-dd): ");
        ingresos.setDescripcionIngreso(readUtil.read()  );

        /* ==== VINCULACIÓN AL USUARIO ==== */
        System.out.print("> ID del usuario que registra el ingreso: ");
        Integer idUsuario = readUtil.readInt();

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