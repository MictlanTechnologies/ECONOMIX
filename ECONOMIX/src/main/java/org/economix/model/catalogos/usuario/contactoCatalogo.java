package org.economix.model.catalogos.usuario;

import org.economix.model.usuario.Persona;
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.usuario.ContactoHiberImpl;
import org.economix.sql.hibernateimpl.usuario.PersonaHiberImpl;
import org.economix.model.usuario.Contacto;
import org.economix.model.usuario.Usuario;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

public class contactoCatalogo extends gestorCatalogos<Contacto> {

    private static contactoCatalogo contactoCatalogo;

    private static final GenericSql<Contacto> contactoHiber = ContactoHiberImpl.getInstance();
    private static final GenericSql<Persona> personaHiber = PersonaHiberImpl.getInstance();

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
        System.out.print("> Teclee su número de celular: ");
        contacto.setNumCelular(readUtil.read());

        System.out.print("> Teclee su correo electrónico: ");
        contacto.setCorreo(readUtil.read());

        /* ==== VINCULACIÓN A LA PERSONA ==== */
        System.out.print("> ID de la persona que registra el contacto: ");
        Integer idPersona = readUtil.readInt();

        Persona persona = personaHiber.findById(idPersona);
        if (persona == null) {
            System.out.println("> ✖ No existe esa persona. No se guardó el contacto.");
            return false;
        }
        contacto.setPersona(persona);        // ★ clave

        /* === guarda === */
        contactoHiber.save(contacto);
        return true;
    }

    @Override
    public boolean processEditT(Contacto contacto) {
        System.out.print("> Teclee su nuevo número de celular: ");
        contacto.setNumCelular(readUtil.read());

        System.out.print("> Teclee su nuevo correo electrónico: ");
        contacto.setCorreo(readUtil.read());

        System.out.print("> ID de la persona (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Persona u = personaHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Persona no existe; se mantiene el actual.");
            } else {
                contacto.setPersona(u);
            }
        }
        contactoHiber.update(contacto);
        return true;
    }

}