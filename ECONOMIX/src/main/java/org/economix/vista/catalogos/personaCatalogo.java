package org.economix.vista.catalogos;

import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.PersonaHiberImpl;
import org.economix.usuario.Persona;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

public class personaCatalogo extends gestorCatalogos<Persona> {
    private static personaCatalogo personaCatalogo;
    private static final GenericSql<Persona> personaHiber = PersonaHiberImpl.getInstance();

    public static personaCatalogo getInstance() {
        if (personaCatalogo == null) {
            personaCatalogo = new personaCatalogo();
        }
        return personaCatalogo;
    }

    private personaCatalogo() {
        super(PersonaHiberImpl.getInstance());
    }

    @Override
    public Persona newT() {
        return new Persona();
    }

    @Override
    public boolean processNewT(Persona persona) {
        System.out.print("> Teclee el nombre del gasto: ");
        persona.setPersona(readUtil.read());
        personaHiber.save(persona);
        return true;
    }

    @Override
    public boolean processEditT(Persona persona) {
        System.out.print("> Ingrese el nuevo nombre del gasto: ");
        persona.setPersona( readUtil.read() );
        personaHiber.update(persona);
        return true;
    }
}