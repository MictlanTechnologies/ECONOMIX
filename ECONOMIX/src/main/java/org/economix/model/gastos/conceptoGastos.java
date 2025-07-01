// Paquete que contiene entidades relacionadas con gastos.
package org.economix.model.gastos;

// Importaciones necesarias para JPA, serialización y utilidades con Lombok.
import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidad que representa un concepto asociado a un gasto.
 * Hereda de {Catalogo}, se almacena en la tabla `tbl_conceptogastos`.
 */
@AttributeOverride(
        name = "id",
        column = @Column(name = "idConcepto")  // Se sobreescribe el nombre del ID heredado
)
@Entity
@Table(name = "tbl_conceptogastos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class conceptoGastos extends Catalogo implements Serializable {

    /**
     * Devuelve el ID del gasto asociado (solo lectura).
     */
    @ToString.Include(name = "idGastos")
    public Integer getIdGastos() {
        return gastos != null ? gastos.getId() : null;
    }

    /** Nombre del concepto */
    @ToString.Include
    @Column(name = "nombreConcepto", length = 100)
    private String nombreConcepto;

    /** Descripción del concepto */
    @ToString.Include
    @Column(name = "descripciónConcepto", columnDefinition = "TEXT")
    private String descripcionConcepto;

    /** Precio del concepto */
    @ToString.Include
    @Column(name = "precioConcepto", precision = 10, scale = 2)
    private BigDecimal precioConcepto;

    /** Relación muchos-a-uno con Gasto (obligatoria) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idGastos", nullable = false)
    @ToString.Exclude
    private Gastos gastos;
}
