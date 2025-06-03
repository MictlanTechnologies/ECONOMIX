package org.economix.vista.catalogos.ingresos;

import org.economix.Ingresos.Ingresos;
import org.economix.Ingresos.conceptoIngresos;
import org.economix.sql.hibernateimpl.ingresos.ConceptoIngresosHiberImpl;
import org.economix.sql.hibernateimpl.ingresos.IngresosHiberImpl;
import org.economix.sql.GenericSql;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

public class conceptoIngresosCatalogo extends gestorCatalogos<conceptoIngresos> {

    private static conceptoIngresosCatalogo conceptoIngresosCatalogo;

    /** repositorios */
    private static final GenericSql<conceptoIngresos>   conceptoIngresosHiber   = ConceptoIngresosHiberImpl.getInstance();
    private static final GenericSql<Ingresos> ingresosHiber = IngresosHiberImpl.getInstance(); // ★ NUEVO

    /** singleton */
    public static conceptoIngresosCatalogo getInstance() {
        if (conceptoIngresosCatalogo == null) {
            conceptoIngresosCatalogo = new conceptoIngresosCatalogo();
        }
        return conceptoIngresosCatalogo;
    }

    private conceptoIngresosCatalogo() {
        super(ConceptoIngresosHiberImpl.getInstance());
    }

    @Override
    public conceptoIngresos newT() {
        return new conceptoIngresos();
    }

    @Override
    public boolean processNewT(conceptoIngresos conceptoIngresos) {
        System.out.print("> Teclee el nombre del artículo: ");
        conceptoIngresos.setNombreConcepto(readUtil.read());

        System.out.print("> Descripción: ");
        conceptoIngresos.setDescripcionConcepto(readUtil.read());

        System.out.print("> Monto: ");
        conceptoIngresos.setPrecioConcepto(readUtil.readBigDecimal());

        /* ==== VINCULACIÓN AL USUARIO ==== */
        System.out.print("> ID del Ingresos que registra el concepto: ");
        Integer idUsuario = readUtil.readInt();

        Ingresos ingresos = ingresosHiber.findById(idUsuario);
        if (ingresos == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó el gasto.");
            return false;
        }
        conceptoIngresos.setIngresos(ingresos);        // ★ clave

        /* === guarda === */
        conceptoIngresosHiber.save(conceptoIngresos);
        return true;
    }

    @Override
    public boolean processEditT(conceptoIngresos conceptoIngresos) {
        System.out.print("> Teclee el nombre del artículo: ");
        conceptoIngresos.setNombreConcepto(readUtil.read());

        System.out.print("> Descripción: ");
        conceptoIngresos.setDescripcionConcepto(readUtil.read());

        System.out.print("> Monto: ");
        conceptoIngresos.setPrecioConcepto(readUtil.readBigDecimal());

        System.out.print("> ID del nuevo Ingreso (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Ingresos u = ingresosHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Usuario no existe; se mantiene el actual.");
            } else {
                conceptoIngresos.setIngresos(u);
            }
        }
        /* === guarda === */
        conceptoIngresosHiber.save(conceptoIngresos);
        return true;
    }
}