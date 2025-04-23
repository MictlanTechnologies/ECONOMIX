package org.economix.vista.catalogos;

import org.economix.sql.GenericSql;
import org.economix.gastos.Gastos;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

public class gastosCatalogo extends gestorCatalogos<Gastos> {
    private static gastosCatalogo gastosCatalogo;
    private static final GenericSql<Gastos> estadoSql = EstadoHiberImpl.getInstance();

    public static gastosCatalogo getInstance() {
        if (gastosCatalogo == null) {
            gastosCatalogo = new gastosCatalogo();
        }
        return gastosCatalogo;
    }

    private gastosCatalogo() {
        super(EstadoHiberImpl.getInstance());
    }

    @Override
    public Gastos newT() {
        return new Gastos();
    }

    @Override
    public boolean processNewT(Gastos gastos) {
        System.out.print("> Teclee el nombre del gasto: ");
        gastos.setGastos(readUtil.read());
        estadoSql.save(gastos);
        return true;
    }

    @Override
    public boolean processEditT(Gastos gastos) {
        System.out.print("> Ingrese el nuevo nombre del gasto: ");
        gastos.setGastos( readUtil.read() );
        estadoSql.update(gastos);
        return true;
    }
}