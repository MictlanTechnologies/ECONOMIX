// Paquete que contiene entidades relacionadas con ingresos.
package org.economix.model.ingresos;

// Importaciones necesarias para JPA, Lombok y tipos de datos.
import org.economix.model.Catalogo;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidad que representa un concepto detallado de ingreso.
 * Se almacena en la tabla `tbl_conceptoingresos`.
 * Cada concepto está vinculado a un ingreso registrado.
 */
@AttributeOverride(
        name = "id",
        column = @Column(name = "idConcepto")  // Reemplaza el campo 'id' heredado
)
@Entity
@Table(name = "tbl_conceptoingresos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class conceptoIngresos extends Catalogo implements Serializable {

    /**
     * Devuelve el ID del ingreso al que pertenece este concepto.
     */
    @ToString.Include(name = "idIngresos")
    public Integer getIdIngresos() {
        return ingresos != null ? ingresos.getId() : null;
    }

    /** Nombre del concepto de ingreso */
    @ToString.Include
    @Column(name = "nombreConcepto", length = 100)
    private String nombreConcepto;

    /** Descripción del concepto de ingreso */
    @ToString.Include
    @Column(name = "descripcionConcepto", columnDefinition = "TEXT")
    private String descripcionConcepto;

    /** Precio o valor económico del concepto */
    @ToString.Include
    @Column(name = "precioConcepto", precision = 10, scale = 2)
    private BigDecimal precioConcepto;

    /** Relación muchos-a-uno con el ingreso principal */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idIngresos", nullable = false)
    @ToString.Exclude
    private Ingresos ingresos;
}
