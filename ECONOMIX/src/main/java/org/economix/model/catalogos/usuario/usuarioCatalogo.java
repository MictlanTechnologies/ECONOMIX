package org.economix.model.catalogos.usuario;


import org.economix.sql.GenericSql;
import org.economix.model.usuario.Usuario;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;
import org.economix.sql.hibernateimpl.usuario.UsuarioHiberImpl;



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
        System.out.print("> Teclee su nombre de Perfil: ");
        usuario.setPerfilUsuario(readUtil.read());

        System.out.print("> Teclee su contraseña de Perfil: ");
        usuario.setContraseñaUsuario(readUtil.read());

        usuarioHiber.save(usuario);
        return true;
    }

    @Override
    public boolean processEditT(Usuario usuario) {
        System.out.print("> Teclee su nuevo nombre de Perfil: ");
        usuario.setPerfilUsuario(readUtil.read());

        System.out.print("> Teclee su nueva contraseña de Perfil: ");
        usuario.setContraseñaUsuario(readUtil.read());

        usuarioHiber.update(usuario);
        return true;
    }
}