// Paquete que contiene los catálogos relacionados con gastos (versión consola).
package org.economix.model.catalogos.gastos;

// Importación de entidades, controladores y utilidades necesarias.
import org.economix.model.gastos.Gastos;
import org.economix.model.gastos.conceptoGastos;
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.gastos.GastosHiberImpl;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;
import org.economix.sql.hibernateimpl.gastos.ConceptoGastosHiberImpl;

/**
 * Clase que representa el catálogo de conceptos de gasto.
 * Permite gestionar operaciones CRUD sobre {conceptoGastos} desde la consola.
 * Se implementa como singleton y extiende {gestorCatalogos}.
 */
public class conceptoGastosCatalogo extends gestorCatalogos<conceptoGastos> {

    /** Instancia única (singleton) del catálogo */
    private static conceptoGastosCatalogo conceptoGastosCatalogo;

    /** Repositorio para acceder a la tabla de conceptos de gasto */
    private static final GenericSql<conceptoGastos> conceptoGastosHiber = ConceptoGastosHiberImpl.getInstance();

    /** Repositorio para acceder a la tabla de gastos (necesario para relacionar conceptos) */
    private static final GenericSql<Gastos> gastosHiber = GastosHiberImpl.getInstance();

    /**
     * Devuelve la instancia única del catálogo de conceptos de gasto.
     * devuelve instancia singleton.
     */
    public static conceptoGastosCatalogo getInstance() {
        if (conceptoGastosCatalogo == null) {
            conceptoGastosCatalogo = new conceptoGastosCatalogo();
        }
        return conceptoGastosCatalogo;
    }

    /**
     * Constructor privado que inicializa el catálogo con su repositorio específico.
     */
    private conceptoGastosCatalogo() {
        super(ConceptoGastosHiberImpl.getInstance());
    }

    /**
     * Crea un nuevo objeto vacío del tipo {@link conceptoGastos}.
     * devuelve nueva instancia de concepto de gasto.
     */
    @Override
    public conceptoGastos newT() {
        return new conceptoGastos();
    }

    /**
     * Procesa el ingreso de datos por consola para crear un nuevo concepto de gasto.
     * Verifica que el ID del gasto asociado exista antes de guardar.
     * @param conceptoGastos entidad a llenar y guardar.
     * @return true si se guarda correctamente, false si el gasto no existe.
     */
    @Override
    public boolean processNewT(conceptoGastos conceptoGastos) {
        System.out.print("> Teclee el nombre del concepto del Gasto: ");
        conceptoGastos.setNombreConcepto(readUtil.read());

        System.out.print("> Descripción: ");
        conceptoGastos.setDescripcionConcepto(readUtil.read());

        System.out.print("> Precio: ");
        conceptoGastos.setPrecioConcepto(readUtil.readBigDecimal());

        // Relación con gasto existente
        System.out.print("> ID del Gasto que registra el concepto: ");
        Integer idGasto = readUtil.readInt();
        Gastos gastos = gastosHiber.findById(idGasto);
        if (gastos == null) {
            System.out.println("> ✖ No existe ese gasto. No se guardó el concepto.");
            return false;
        }
        conceptoGastos.setGastos(gastos);

        // Guardado en base de datos
        conceptoGastosHiber.save(conceptoGastos);
        return true;
    }

    /**
     * Procesa la edición de un concepto de gasto mediante entrada por consola.
     * Permite actualizar todos los campos, incluido el gasto asociado si se desea.
     * @param conceptoGastos entidad que se va a editar.
     * devuelve true si se edita correctamente.
     */
    @Override
    public boolean processEditT(conceptoGastos conceptoGastos) {
        System.out.print("> Teclee el nuevo nombre del concepto del Gasto: ");
        conceptoGastos.setNombreConcepto(readUtil.read());

        System.out.print("> Nueva Descripción: ");
        conceptoGastos.setDescripcionConcepto(readUtil.read());

        System.out.print("> Nuevo Precio: ");
        conceptoGastos.setPrecioConcepto(readUtil.readBigDecimal());

        System.out.print("> ID del Gasto (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Gastos u = gastosHiber.findById(Integer.parseInt(inp));
            if (u == null) {
                System.out.println("> Gasto no existe; se mantiene el actual.");
            } else {
                conceptoGastos.setGastos(u);
            }
        }

        conceptoGastosHiber.update(conceptoGastos);
        return true;
    }
}
