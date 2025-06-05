package org.economix.model.ingresos;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@AttributeOverride(
        name = "id",
        column = @Column(name = "idPresupuesto")
)
@Entity
@Table(name = "tbl_presupuesto")                       // ← igual que en la BD
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Presupuesto extends Catalogo implements Serializable {

    @ToString.Include(name = "idIngresos")        // ★
    public Integer getIdIngresos() {
        return ingresos != null ? ingresos.getId() : null;
    }

    @ToString.Include
    @Column(name = "fechaPresupuesto")
    private Date fechaPresupuesto;

    @ToString.Include
    @Column(name = "fechaActualizaciónP")
    private Date fechaActualizacionP;

    @ToString.Include
    @Column(name = "periodoTPresupuesto", length = 50)
    private String periodoTPresupuesto;

    @ToString.Include
    @Column(name = "montoPresupuesto", precision = 10, scale = 2)
    private BigDecimal montoPresupuesto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idIngresos", nullable = false)
    @ToString.Exclude
    private Ingresos ingresos;
}
