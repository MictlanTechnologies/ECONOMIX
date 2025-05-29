package org.economix.vista.catalogos;

import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.DomicilioHiberImpl;
import org.economix.usuario.Domicilio;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

public class domicilioCatalogo extends gestorCatalogos<Domicilio> {
    private static domicilioCatalogo domicilioCatalogo;
    private static final GenericSql<Domicilio> domicilioHiber = DomicilioHiberImpl.getInstance();

    public static domicilioCatalogo getInstance() {
        if (domicilioCatalogo == null) {
            domicilioCatalogo = new domicilioCatalogo();
        }
        return domicilioCatalogo;
    }

    private domicilioCatalogo() {
        super(DomicilioHiberImpl.getInstance());
    }

    @Override
    public Domicilio newT() {
        return new Domicilio();
    }

    @Override
    public boolean processNewT(Domicilio domicilio) {
        System.out.print("> Teclee el nombre del domicilio: ");
        domicilio.set(readUtil.read());
        domicilioHiber.save(domicilio);
        return true;
    }

    @Override
    public boolean processEditT(Domicilio domicilio) {
        System.out.print("> Ingrese el nuevo nombre del domicilio: ");
        domicilio.setDomicilio( readUtil.read() );
        domicilioHiber.update(domicilio);
        return true;
    }
}