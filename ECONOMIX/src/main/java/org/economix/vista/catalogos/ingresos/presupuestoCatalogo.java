package org.economix.vista.catalogos.ingresos;

import org.economix.ingresos.Ingresos;
import org.economix.ingresos.Presupuesto;
import org.economix.sql.hibernateimpl.ingresos.IngresosHiberImpl;
import org.economix.sql.hibernateimpl.ingresos.PresupuestoHiberImpl;
import org.economix.sql.GenericSql;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

import java.sql.Date;

public class presupuestoCatalogo extends gestorCatalogos<Presupuesto> {

    private static presupuestoCatalogo presupuestoCatalogo;

    /** repositorios */
    private static final GenericSql<Presupuesto>   presupuestoHiber   = PresupuestoHiberImpl.getInstance();
    private static final GenericSql<Ingresos> ingresosHiber = IngresosHiberImpl.getInstance(); // ★ NUEVO

    /** singleton */
    public static presupuestoCatalogo getInstance() {
        if (presupuestoCatalogo == null) {
            presupuestoCatalogo = new presupuestoCatalogo();
        }
        return presupuestoCatalogo;
    }

    private presupuestoCatalogo() {
        super(PresupuestoHiberImpl.getInstance());
    }

    @Override
    public Presupuesto newT() {
        return new Presupuesto();
    }

    @Override
    public boolean processNewT(Presupuesto presupuesto) {
        System.out.print("> Fecha del presupuesto (yyyy-MM-dd):");
        presupuesto.setFechaPresupuesto(Date.valueOf(readUtil.read()));

        System.out.print("> Fecha de la actualización (yyyy-MM-dd): ");
        presupuesto.setFechaActualizacionP(Date.valueOf(readUtil.read()));

        System.out.print("> Periodo: ");
        presupuesto.setPeriodoTPresupuesto(readUtil.read());

        System.out.print("> Monto: ");
        presupuesto.setMontoPresupuesto(readUtil.readBigDecimal());

        /* ==== VINCULACIÓN AL USUARIO ==== */
        System.out.print("> ID del Ingreso que registra el presupuesto: ");
        Integer idIngreso = readUtil.readInt();

        Ingresos ingresos = ingresosHiber.findById(idIngreso);
        if (ingresos == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó el gasto.");
            return false;
        }
        presupuesto.setIngresos(ingresos);        // ★ clave

        /* === guarda === */
        presupuestoHiber.save(presupuesto);
        return true;
    }

    @Override
    public boolean processEditT(Presupuesto presupuesto) {
        System.out.print("> Fecha nueva del presupuesto (yyyy-MM-dd):");
        presupuesto.setFechaPresupuesto(Date.valueOf(readUtil.read()));

        System.out.print("> Fecha nueva de la actualización(yyyy-MM-dd): ");
        presupuesto.setFechaActualizacionP(Date.valueOf(readUtil.read()));

        System.out.print("> Periodo Nuevo: ");
        presupuesto.setPeriodoTPresupuesto(readUtil.read());

        System.out.print("> Monto Nuevo: ");
        presupuesto.setMontoPresupuesto(readUtil.readBigDecimal());

        System.out.print("> ID del nuevo Ingreso (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Ingresos u = ingresosHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Ingreso no existe; se mantiene el actual.");
            } else {
                presupuesto.setIngresos(u);
            }
        }
        presupuestoHiber.update(presupuesto);
        return true;
    }
}