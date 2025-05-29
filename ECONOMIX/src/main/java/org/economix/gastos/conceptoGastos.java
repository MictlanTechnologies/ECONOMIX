package org.economix.gastos;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;
import org.economix.usuario.Usuario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AttributeOverride(
        name = "id",
        column = @Column(name = "idConcepto")
)
@Entity
@Table(name = "tbl_conceptogastos")                       // ← igual que en la BD
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class conceptoGastos extends Catalogo implements Serializable{

    @ToString.Include(name = "idGastos")        // ★
    public Integer getIdGastos() {
        return gastos != null ? gastos.getId() : null;
    }

    @ToString.Include
    @Column(name = "nombreConcepto", length = 100)      // ← default: nullable = true
    private String nombreConcepto;

    @ToString.Include
    @Column(name = "descripciónConcepto", columnDefinition = "TEXT")
    private String descripcionConcepto;

    @ToString.Include
    @Column(name = "precioConcepto", precision = 10, scale = 2)
    private BigDecimal precioConcepto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idGastos", nullable = false)
    @ToString.Exclude
    private Gastos gastos;
}
