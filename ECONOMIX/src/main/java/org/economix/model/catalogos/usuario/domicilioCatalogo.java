package org.economix.model.catalogos.usuario;

import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.usuario.DomicilioHiberImpl;
import org.economix.sql.hibernateimpl.usuario.UsuarioHiberImpl;
import org.economix.model.usuario.Domicilio;
import org.economix.model.usuario.Usuario;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

public class domicilioCatalogo extends gestorCatalogos<Domicilio> {

    private static domicilioCatalogo domicilioCatalogo;

    private static final GenericSql<Domicilio> domicilioHiber = DomicilioHiberImpl.getInstance();
    private static final GenericSql<Usuario>  usuarioHiber  = UsuarioHiberImpl.getInstance();
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
        System.out.print("> Teclee el nombre de la ciudad: ");
        domicilio.setCiudad(readUtil.read());

        System.out.print("> Teclee el nombre de la calle: ");
        domicilio.setCalle(readUtil.read());

        System.out.print("> Teclee el nombre de la colonia: ");
        domicilio.setColonia(readUtil.read());

        System.out.print("> Teclee el número de la vivienda: ");
        domicilio.setNumero(readUtil.read());

        /* ==== VINCULACIÓN AL USUARIO ==== */
        System.out.print("> ID del usuario que registra el gasto: ");
        Integer idUsuario = readUtil.readInt();

        Usuario usuario = usuarioHiber.findById(idUsuario);
        if (usuario == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó el gasto.");
            return false;
        }
        domicilio.setUsuario(usuario);        // ★ clave

        /* === guarda === */
        domicilioHiber.save(domicilio);
        return true;
    }

    @Override
    public boolean processEditT(Domicilio domicilio) {
        System.out.print("> Teclee el nuevo nombre de la ciudad: ");
        domicilio.setCiudad(readUtil.read());

        System.out.print("> Teclee el nuevo nombre de la calle: ");
        domicilio.setCalle(readUtil.read());

        System.out.print("> Teclee el nuevo nombre de la colonia: ");
        domicilio.setColonia(readUtil.read());

        System.out.print("> Teclee el nuevo número de la vivienda: ");
        domicilio.setNumero(readUtil.read());

        /* ==== VINCULACIÓN AL USUARIO ==== */
        System.out.print("> ID del usuario que registra el domicilio: ");
        Integer idUsuario = readUtil.readInt();

        System.out.print("> ID de usuario (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Usuario u = usuarioHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Usuario no existe; se mantiene el actual.");
            } else {
                domicilio.setUsuario(u);
            }
        }
        domicilioHiber.update(domicilio);
        return true;
    }
}