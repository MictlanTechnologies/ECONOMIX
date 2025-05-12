package org.economix.vista.catalogos;

import org.economix.sql.GenericSql;
import org.economix.gastos.Gastos;
import org.economix.sql.hibernateimpl.GastosHiberImpl;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

import java.sql.Date;

public class gastosCatalogo extends gestorCatalogos<Gastos> {
    private static gastosCatalogo gastosCatalogo;
    private static final GenericSql<Gastos> gastosHiber = GastosHiberImpl.getInstance();

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

        gastosHiber.update(gastos);
        return true;
    }
}