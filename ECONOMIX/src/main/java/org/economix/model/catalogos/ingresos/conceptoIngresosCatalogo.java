// Paquete que contiene los catálogos relacionados con ingresos (versión consola).
package org.economix.model.catalogos.ingresos;

// Importación de clases necesarias: entidades, repositorios y utilidades.
import org.economix.model.ingresos.Ingresos;
import org.economix.model.ingresos.conceptoIngresos;
import org.economix.sql.hibernateimpl.ingresos.ConceptoIngresosHiberImpl;
import org.economix.sql.hibernateimpl.ingresos.IngresosHiberImpl;
import org.economix.sql.GenericSql;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

/**
 * Catálogo para la gestión de conceptos de ingresos desde consola.
 * Permite registrar, editar y vincular conceptos con ingresos específicos.
 * Extiende {gestorCatalogos} y aplica el patrón Singleton.
 */
public class conceptoIngresosCatalogo extends gestorCatalogos<conceptoIngresos> {

    /** Instancia singleton del catálogo */
    private static conceptoIngresosCatalogo conceptoIngresosCatalogo;

    /** Repositorio para acceder a la tabla de conceptos de ingresos */
    private static final GenericSql<conceptoIngresos> conceptoIngresosHiber = ConceptoIngresosHiberImpl.getInstance();

    /** Repositorio para acceder a la tabla de ingresos */
    private static final GenericSql<Ingresos> ingresosHiber = IngresosHiberImpl.getInstance();

    /**
     * Obtiene la instancia única del catálogo.
     * devuelve objeto singleton de {@code conceptoIngresosCatalogo}.
     */
    public static conceptoIngresosCatalogo getInstance() {
        if (conceptoIngresosCatalogo == null) {
            conceptoIngresosCatalogo = new conceptoIngresosCatalogo();
        }
        return conceptoIngresosCatalogo;
    }

    /**
     * Constructor privado que configura el repositorio base.
     */
    private conceptoIngresosCatalogo() {
        super(ConceptoIngresosHiberImpl.getInstance());
    }

    /**
     * Crea una nueva instancia vacía de {@link conceptoIngresos}.
     * devuelve nuevo objeto de concepto de ingreso.
     */
    @Override
    public conceptoIngresos newT() {
        return new conceptoIngresos();
    }

    /**
     * Procesa la creación de un nuevo concepto de ingreso desde consola.
     * Solicita al usuario los datos requeridos y vincula el concepto a un ingreso existente.
     * @param conceptoIngresos objeto a llenar y guardar.
     * devuelve true si se guarda correctamente, false si falla la validación.
     */
    @Override
    public boolean processNewT(conceptoIngresos conceptoIngresos) {
        System.out.print("> Teclee el nombre del concepto: ");
        conceptoIngresos.setNombreConcepto(readUtil.read());

        System.out.print("> Descripción del concepto: ");
        conceptoIngresos.setDescripcionConcepto(readUtil.read());

        System.out.print("> Precio del Concepto: ");
        conceptoIngresos.setPrecioConcepto(readUtil.readBigDecimal());

        // Asociación con ingreso existente
        System.out.print("> ID del Ingreso que registra el concepto: ");
        Integer idUsuario = readUtil.readInt();
        Ingresos ingresos = ingresosHiber.findById(idUsuario);
        if (ingresos == null) {
            System.out.println("> ✖ No existe ese ingreso. No se guardó el concepto.");
            return false;
        }
        conceptoIngresos.setIngresos(ingresos);

        // Guardar en base de datos
        conceptoIngresosHiber.save(conceptoIngresos);
        return true;
    }

    /**
     * Procesa la edición de un concepto de ingreso existente.
     * Permite modificar los datos del concepto y cambiar su ingreso asociado.
     * @param conceptoIngresos objeto a modificar.
     * devuelve true si se edita y guarda correctamente.
     */
    @Override
    public boolean processEditT(conceptoIngresos conceptoIngresos) {
        System.out.print("> Teclee el nombre del concepto: ");
        conceptoIngresos.setNombreConcepto(readUtil.read());

        System.out.print("> Descripción del concepto: ");
        conceptoIngresos.setDescripcionConcepto(readUtil.read());

        System.out.print("> Precio del Concepto: ");
        conceptoIngresos.setPrecioConcepto(readUtil.readBigDecimal());

        System.out.print("> ID del Ingreso nuevo para el concepto (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Ingresos u = ingresosHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Ingreso no existe; se mantiene el actual.");
            } else {
                conceptoIngresos.setIngresos(u);
            }
        }

        // Guardar cambios en base de datos
        conceptoIngresosHiber.save(conceptoIngresos);
        return true;
    }
}
