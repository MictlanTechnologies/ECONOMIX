package org.economix.vista.catalogos;

import org.economix.sql.GenericSql;
import org.economix.usuario.Usuario;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;
import org.economix.sql.hibernateimpl.UsuarioHiberImpl;

public class usuarioCatalogo extends gestorCatalogos<Usuario> {
    private static usuarioCatalogo usuarioCatalogo;
    private static final GenericSql<Usuario> usuarioHiber = UsuarioHiberImpl.getInstance();

    public static usuarioCatalogo getInstance() {
        if (usuarioCatalogo == null) {
            usuarioCatalogo = new usuarioCatalogo();
        }
        return usuarioCatalogo;
    }

    private usuarioCatalogo() {
        super(UsuarioHiberImpl.getInstance());
    }

    @Override
    public Usuario newT() {
        return new Usuario();
    }

    @Override
    public boolean processNewT(Usuario usuario) {
        System.out.print("> Teclee el nombre del gasto: ");
        usuario.setUsuario(readUtil.read());
        usuarioHiber.save(usuario);
        return true;
    }

    @Override
    public boolean processEditT(Usuario usuario) {
        System.out.print("> Ingrese el nuevo nombre del gasto: ");
        usuario.setUsuario( readUtil.read() );
        usuarioHiber.update(usuario);
        return true;

    }

}