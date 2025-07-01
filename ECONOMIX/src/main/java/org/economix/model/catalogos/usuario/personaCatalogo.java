// Paquete de catálogos de usuario (versión consola).
package org.economix.model.catalogos.usuario;

// Importación de clases necesarias: entidades, repositorios y utilidades.
import org.economix.model.usuario.Persona;
import org.economix.model.usuario.Usuario;
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.usuario.PersonaHiberImpl;
import org.economix.sql.hibernateimpl.usuario.UsuarioHiberImpl;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

/**
 * Catálogo para la gestión de personas desde consola.
 * Permite registrar y editar entidades {@link Persona} vinculadas a un {@link Usuario}.
 * Extiende {gestorCatalogos} y aplica el patrón Singleton.
 */
public class personaCatalogo extends gestorCatalogos<Persona> {

    /** Instancia única (singleton) del catálogo */
    private static personaCatalogo personaCatalogo;

    /** Repositorio Hibernate para personas */
    private static final GenericSql<Persona> personaHiber = PersonaHiberImpl.getInstance();

    /** Repositorio Hibernate para usuarios (necesario para vincular) */
    private static final GenericSql<Usuario> usuarioHiber = UsuarioHiberImpl.getInstance();

    /**
     * Devuelve la única instancia del catálogo (singleton).
     * devuelve objeto único {@code personaCatalogo}
     */
    public static personaCatalogo getInstance() {
        if (personaCatalogo == null) {
            personaCatalogo = new personaCatalogo();
        }
        return personaCatalogo;
    }

    /**
     * Constructor privado, define el repositorio base para el catálogo.
     */
    private personaCatalogo() {
        super(PersonaHiberImpl.getInstance());
    }

    /**
     * Crea una nueva entidad {@link Persona} vacía.
     * devuelve nueva instancia de Persona.
     */
    @Override
    public Persona newT() {
        return new Persona();
    }

    /**
     * Procesa el registro de una nueva persona desde consola.
     * Solicita los datos necesarios y asocia a un usuario existente.
     * @param persona objeto Persona a llenar.
     * devuelve true si se guarda correctamente, false si falla la validación.
     */
    @Override
    public boolean processNewT(Persona persona) {
        System.out.print("> Teclee su nombre: ");
        persona.setNombreP(readUtil.read());

        System.out.print("> Teclee su apellido paterno: ");
        persona.setApellidoP(readUtil.read());

        System.out.print("> Teclee su apellido materno: ");
        persona.setApellidoM(readUtil.read());

        // Vinculación con un usuario existente
        System.out.print("> ID del usuario que registra a la persona: ");
        Integer idUsuario = readUtil.readInt();
        Usuario usuario = usuarioHiber.findById(idUsuario);
        if (usuario == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó a la persona.");
            return false;
        }
        persona.setUsuario(usuario);

        personaHiber.save(persona);
        return true;
    }

    /**
     * Procesa la edición de una persona existente.
     * Permite modificar datos personales y cambiar el usuario si se desea.
     * @param persona objeto Persona a modificar.
     * devuelve true si se actualiza correctamente.
     */
    @Override
    public boolean processEditT(Persona persona) {
        System.out.print("> Teclee su nuevo nombre: ");
        persona.setNombreP(readUtil.read());

        System.out.print("> Teclee su nuevo apellido paterno: ");
        persona.setApellidoP(readUtil.read());

        System.out.print("> Teclee su nuevo apellido materno: ");
        persona.setApellidoM(readUtil.read());

        // Opción de cambiar usuario vinculado
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
