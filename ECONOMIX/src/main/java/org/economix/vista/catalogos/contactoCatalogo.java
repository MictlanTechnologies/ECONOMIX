package org.economix.vista.catalogos;

import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.ContactoHiberImpl;
import org.economix.sql.hibernateimpl.UsuarioHiberImpl;
import org.economix.usuario.Contacto;
import org.economix.usuario.Usuario;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

public class contactoCatalogo extends gestorCatalogos<Contacto> {

    private static contactoCatalogo contactoCatalogo;

    private static final GenericSql<Contacto> contactoHiber = ContactoHiberImpl.getInstance();
    private static final GenericSql<Usuario>  usuarioHiber  = UsuarioHiberImpl.getInstance();

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

        /* ==== VINCULACIÓN AL USUARIO ==== */
        System.out.print("> ID del usuario que registra el gasto: ");
        Integer idUsuario = readUtil.readInt();

        Usuario usuario = usuarioHiber.findById(idUsuario);
        if (usuario == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó el gasto.");
            return false;
        }
        contacto.setUsuario(usuario);        // ★ clave

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

        System.out.print("> ID de usuario (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Usuario u = usuarioHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Usuario no existe; se mantiene el actual.");
            } else {
                contacto.setUsuario(u);
            }
        }
        contactoHiber.update(contacto);
        return true;
    }

}