package org.economix.vista.catalogos.usuario;

import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.usuario.PersonaHiberImpl;
import org.economix.sql.hibernateimpl.usuario.UsuarioHiberImpl;
import org.economix.usuario.Persona;
import org.economix.usuario.Usuario;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

public class personaCatalogo extends gestorCatalogos<Persona> {

    private static personaCatalogo personaCatalogo;

    private static final GenericSql<Persona> personaHiber = PersonaHiberImpl.getInstance();
    private static final GenericSql<Usuario>  usuarioHiber  = UsuarioHiberImpl.getInstance();

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
    public boolean processNewT(Persona persona)
    {
        System.out.print("> Teclee su nombre: ");
        persona.setNombreP(readUtil.read());

        System.out.print("> Teclee su apellido paterno: ");
        persona.setApellidoP(readUtil.read());

        System.out.print("> Teclee su apellido materno: ");
        persona.setApellidoM(readUtil.read());

        /* ==== VINCULACIÓN AL USUARIO ==== */
        System.out.print("> ID del usuario que registra a la persona: ");
        Integer idUsuario = readUtil.readInt();

        Usuario usuario = usuarioHiber.findById(idUsuario);
        if (usuario == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó a la persona.");
            return false;
        }
        persona.setUsuario(usuario);        // ★ clave

        /* === guarda === */
        personaHiber.save(persona);
        return true;
    }

    @Override
    public boolean processEditT(Persona persona)
    {

        System.out.print("> Teclee su nuevo nombre: ");
        persona.setNombreP(readUtil.read());

        System.out.print("> Teclee su nuevo apellido paterno: ");
        persona.setApellidoP(readUtil.read());

        System.out.print("> Teclee su nuevo apellido materno: ");
        persona.setApellidoM(readUtil.read());

        System.out.print("> ID de usuario (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Usuario u = usuarioHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Usuario no existe; se mantiene el actual.");
            } else {
                persona.setUsuario(u);
            }
        }
        personaHiber.update(persona);
        return true;
    }
}