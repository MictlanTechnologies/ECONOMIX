// Paquete que contiene los catálogos relacionados con usuarios (versión consola).
package org.economix.model.catalogos.usuario;

// Importación de clases necesarias: entidades, repositorios y utilidades.
import org.economix.model.usuario.Usuario;
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.usuario.UsuarioHiberImpl;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

/**
 * Catálogo para la gestión de usuarios desde consola.
 * Permite registrar y editar entidades {@link Usuario}.
 * Extiende {gestorCatalogos} y aplica patrón Singleton.
 */
public class usuarioCatalogo extends gestorCatalogos<Usuario> {

    /** Instancia única del catálogo (singleton) */
    private static usuarioCatalogo usuarioCatalogo;

    /** Repositorio Hibernate para usuarios */
    private static final GenericSql<Usuario> usuarioHiber = UsuarioHiberImpl.getInstance();

    /**
     * Devuelve la única instancia del catálogo.
     * devuelve objeto {@code usuarioCatalogo}
     */
    public static usuarioCatalogo getInstance() {
        if (usuarioCatalogo == null) {
            usuarioCatalogo = new usuarioCatalogo();
        }
        return usuarioCatalogo;
    }

    /**
     * Constructor privado que inicializa el repositorio base.
     */
    private usuarioCatalogo() {
        super(UsuarioHiberImpl.getInstance());
    }

    /**
     * Crea una nueva instancia vacía de {@link Usuario}.
     * devuelve nuevo objeto Usuario.
     */
    @Override
    public Usuario newT() {
        return new Usuario();
    }

    /**
     * Procesa el registro de un nuevo usuario desde consola.
     * Solicita nombre de perfil y contraseña.
     * @param usuario entidad a llenar.
     * devuelve true si se guarda exitosamente.
     */
    @Override
    public boolean processNewT(Usuario usuario) {
        System.out.print("> Teclee su nombre de Perfil: ");
        usuario.setPerfilUsuario(readUtil.read());

        System.out.print("> Teclee su contraseña de Perfil: ");
        usuario.setContraseñaUsuario(readUtil.read());

        usuarioHiber.save(usuario);
        return true;
    }

    /**
     * Procesa la edición de un usuario existente.
     * Permite cambiar perfil y contraseña.
     * @param usuario entidad a modificar.
     * devuelve true si se actualiza exitosamente.
     */
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
