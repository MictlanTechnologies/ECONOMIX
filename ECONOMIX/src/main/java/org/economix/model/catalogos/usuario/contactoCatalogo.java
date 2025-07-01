// Paquete que contiene catálogos de usuario relacionados (versión consola).
package org.economix.model.catalogos.usuario;

// Importación de clases necesarias: entidades, repositorios y utilidades.
import org.economix.model.usuario.Persona;
import org.economix.model.usuario.Contacto;
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.usuario.ContactoHiberImpl;
import org.economix.sql.hibernateimpl.usuario.PersonaHiberImpl;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

/**
 * Catálogo para la gestión de contactos en consola.
 * Permite registrar y editar información de contacto de una persona.
 * Extiende {gestorCatalogos} aplicando el patrón Singleton.
 */
public class contactoCatalogo extends gestorCatalogos<Contacto> {

    /** Instancia singleton del catálogo */
    private static contactoCatalogo contactoCatalogo;

    /** Repositorio para acceder a la tabla de contactos */
    private static final GenericSql<Contacto> contactoHiber = ContactoHiberImpl.getInstance();

    /** Repositorio para acceder a la tabla de personas */
    private static final GenericSql<Persona> personaHiber = PersonaHiberImpl.getInstance();

    /**
     * Obtiene la instancia única del catálogo.
     * regresa objeto singleton de {@code contactoCatalogo}.
     */
    public static contactoCatalogo getInstance() {
        if (contactoCatalogo == null) {
            contactoCatalogo = new contactoCatalogo();
        }
        return contactoCatalogo;
    }

    /**
     * Constructor privado que configura el repositorio base.
     */
    private contactoCatalogo() {
        super(ContactoHiberImpl.getInstance());
    }

    /**
     * Crea una nueva instancia vacía de {@link Contacto}.
     * regresa nuevo objeto de contacto.
     */
    @Override
    public Contacto newT() {
        return new Contacto();
    }

    /**
     * Procesa la creación de un nuevo contacto desde consola.
     * Solicita al usuario los datos del contacto y lo vincula a una persona existente.
     * @param contacto objeto de contacto a llenar.
     * devuelve true si se guarda correctamente, false si falla la validación.
     */
    @Override
    public boolean processNewT(Contacto contacto) {
        System.out.print("> Teclee su número de celular: ");
        contacto.setNumCelular(readUtil.read());

        System.out.print("> Teclee su correo electrónico: ");
        contacto.setCorreo(readUtil.read());

        // Asociación con persona existente
        System.out.print("> ID de la persona que registra el contacto: ");
        Integer idPersona = readUtil.readInt();
        Persona persona = personaHiber.findById(idPersona);
        if (persona == null) {
            System.out.println("> ✖ No existe esa persona. No se guardó el contacto.");
            return false;
        }
        contacto.setPersona(persona);

        // Guardar en base de datos
        contactoHiber.save(contacto);
        return true;
    }

    /**
     * Procesa la edición de un contacto existente.
     * Permite modificar la información de contacto y cambiar la persona asociada si se desea.
     * @param contacto objeto de contacto a modificar.
     * devuelve true si se edita y guarda correctamente.
     */
    @Override
    public boolean processEditT(Contacto contacto) {
        System.out.print("> Teclee su nuevo número de celular: ");
        contacto.setNumCelular(readUtil.read());

        System.out.print("> Teclee su nuevo correo electrónico: ");
        contacto.setCorreo(readUtil.read());

        // Posible cambio de persona asociada
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
