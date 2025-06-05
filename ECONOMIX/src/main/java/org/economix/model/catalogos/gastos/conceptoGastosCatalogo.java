package org.economix.model.catalogos.gastos;

import org.economix.model.gastos.Gastos;
import org.economix.model.gastos.conceptoGastos;
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.gastos.GastosHiberImpl;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;
import org.economix.sql.hibernateimpl.gastos.ConceptoGastosHiberImpl;

public class conceptoGastosCatalogo extends gestorCatalogos<conceptoGastos> {

    private static conceptoGastosCatalogo conceptoGastosCatalogo;

    /** repositorios */
    private static final GenericSql<conceptoGastos> conceptoGastosHiber = ConceptoGastosHiberImpl.getInstance();
    private static final GenericSql<Gastos>  gastosHiber  = GastosHiberImpl.getInstance(); // ★ NUEVO

    /** singleton */
    public static conceptoGastosCatalogo getInstance() {
        if (conceptoGastosCatalogo == null) {
            conceptoGastosCatalogo = new conceptoGastosCatalogo();
        }
        return conceptoGastosCatalogo;
    }

    private conceptoGastosCatalogo() {
        super(ConceptoGastosHiberImpl.getInstance());
    }

    @Override
    public conceptoGastos newT() {
        return new conceptoGastos();
    }

    @Override
    public boolean processNewT(conceptoGastos conceptoGastos) {
        System.out.print("> Teclee el nombre del concepto del Gasto: ");
        conceptoGastos.setNombreConcepto(readUtil.read());

        System.out.print("> Descripción: ");
        conceptoGastos.setDescripcionConcepto(readUtil.read());

        System.out.print("> Precio: ");
        conceptoGastos.setPrecioConcepto(readUtil.readBigDecimal());

        /* ==== VINCULACIÓN AL USUARIO ==== */
        System.out.print("> ID del Gasto que registra el concepto: ");
        Integer idGasto = readUtil.readInt();

        Gastos gastos = gastosHiber.findById(idGasto);
        if (gastos == null) {
            System.out.println("> ✖ No existe ese gasto. No se guardó el concepto.");
            return false;
        }
        conceptoGastos.setGastos(gastos);        // ★ clave

        /* === guarda === */
        conceptoGastosHiber.save(conceptoGastos);
        return true;
    }

    @Override
    public boolean processEditT(conceptoGastos conceptoGastos) {
        System.out.print("> Teclee el nuevo nombre del concepto del Gasto: ");
        conceptoGastos.setNombreConcepto(readUtil.read());

        System.out.print("> Nueva Descripción: ");
        conceptoGastos.setDescripcionConcepto(readUtil.read());

        System.out.print("> Nuevo Precio: ");
        conceptoGastos.setPrecioConcepto(readUtil.readBigDecimal());
        System.out.print("> ID del Gasto (ENTER para dejar igual): ");
        String inp = readUtil.read();

        if (!inp.isBlank()) {
            Gastos u = gastosHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Gasto no existe; se mantiene el actual.");
            } else {
                conceptoGastos.setGastos(u);
            }
        }
        conceptoGastosHiber.update(conceptoGastos);
        return true;
    }
}
