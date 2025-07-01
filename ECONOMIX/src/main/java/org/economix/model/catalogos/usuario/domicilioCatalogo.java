// Paquete que contiene catálogos de usuario relacionados (versión consola).
package org.economix.model.catalogos.usuario;

// Importación de clases necesarias: entidades, repositorios y utilidades.
import org.economix.model.usuario.Persona;
import org.economix.model.usuario.Domicilio;
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.usuario.DomicilioHiberImpl;
import org.economix.sql.hibernateimpl.usuario.PersonaHiberImpl;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

/**
 * Catálogo para la gestión de domicilios desde consola.
 * Permite registrar y editar direcciones vinculadas a personas.
 * Extiende {gestorCatalogos} aplicando el patrón Singleton.
 */
public class domicilioCatalogo extends gestorCatalogos<Domicilio> {

    /** Instancia singleton del catálogo */
    private static domicilioCatalogo domicilioCatalogo;

    /** Repositorio para acceder a la tabla de domicilios */
    private static final GenericSql<Domicilio> domicilioHiber = DomicilioHiberImpl.getInstance();

    /** Repositorio para acceder a la tabla de personas */
    private static final GenericSql<Persona> personaHiber = PersonaHiberImpl.getInstance();

    /**
     * Obtiene la instancia única del catálogo.
     * regresa objeto singleton de {@code domicilioCatalogo}.
     */
    public static domicilioCatalogo getInstance() {
        if (domicilioCatalogo == null) {
            domicilioCatalogo = new domicilioCatalogo();
        }
        return domicilioCatalogo;
    }

    /**
     * Constructor privado que configura el repositorio base.
     */
    private domicilioCatalogo() {
        super(DomicilioHiberImpl.getInstance());
    }

    /**
     * Crea una nueva instancia vacía de {@link Domicilio}.
     * regresa nuevo objeto de domicilio.
     */
    @Override
    public Domicilio newT() {
        return new Domicilio();
    }

    /**
     * Procesa la creación de un nuevo domicilio desde consola.
     * Solicita los datos de dirección y vincula el domicilio a una persona existente.
     * @param domicilio objeto domicilio a llenar.
     * regresa true si se guarda correctamente, false si falla la validación.
     */
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

        // Asociación con persona existente
        System.out.print("> ID de la persona que registra el domicilio: ");
        Integer idPersona = readUtil.readInt();
        Persona persona = personaHiber.findById(idPersona);
        if (persona == null) {
            System.out.println("> ✖ No existe esa persona. No se guardó el domicilio.");
            return false;
        }
        domicilio.setPersona(persona);

        // Guardar en base de datos
        domicilioHiber.save(domicilio);
        return true;
    }

    /**
     * Procesa la edición de un domicilio existente.
     * Permite modificar los datos de dirección y la persona asociada si se desea.
     * @param domicilio objeto domicilio a modificar.
     * regresa true si se actualiza correctamente.
     */
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

        // Posible cambio de persona asociada
        System.out.print("> ID de usuario (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Persona u = personaHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Persona no existe; se mantiene el actual.");
            } else {
                domicilio.setPersona(u);
            }
        }

        domicilioHiber.update(domicilio);
        return true;
    }
}
