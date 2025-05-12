package org.economix.vista.catalogos;

import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.ContactoHiberImpl;
import org.economix.usuario.Contacto;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

public class contactoCatalogo extends gestorCatalogos<Contacto> {
    private static contactoCatalogo contactoCatalogo;
    private static final GenericSql<Contacto> contactoHiber = ContactoHiberImpl.getInstance();

    public static contactoCatalogo getInstance() {
        if (contactoCatalogo == null) {
            contactoCatalogo = new contactoCatalogo();
        }
        return contactoCatalogo;
    }

    private contactoCatalogo() {
        super(ContactoHiberImpl.getInstance());
    }

    @Override
    public Contacto newT() {
        return new Contacto();
    }

    @Override
    public boolean processNewT(Contacto contacto) {
        System.out.print("> Teclee el nombre del gasto: ");
        contacto.setNumCelular(readUtil.read());
        contactoHiber.save(contacto);
        return true;
    }

    @Override
    public boolean processEditT(Contacto contacto) {
        System.out.print("> Ingrese el nuevo nombre del gasto: ");
        contacto.setNumCelular( readUtil.read() );
        contactoHiber.update(contacto);
        return true;
    }

}