package org.economix.model.ingresos;

import org.economix.model.Catalogo;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;

@AttributeOverride(
        name = "id",
        column = @Column(name = "idConcepto")
)
@Entity
@Table(name = "tbl_conceptoingresos")                       // ← igual que en la BD
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class conceptoIngresos extends Catalogo implements Serializable {

    @ToString.Include(name = "idIngresos")        // ★
    public Integer getIdIngresos() {
        return ingresos != null ? ingresos.getId() : null;
    }

    @ToString.Include
    @Column(name = "nombreConcepto", length = 100)      // ← default: nullable = true
    private String nombreConcepto;

    @ToString.Include
    @Column(name = "descripcionConcepto", columnDefinition = "TEXT")
    private String descripcionConcepto;

    @ToString.Include
    @Column(name = "precioConcepto", precision = 10, scale = 2)
    private BigDecimal precioConcepto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idIngresos", nullable = false)
    @ToString.Exclude
    private Ingresos ingresos;

}
