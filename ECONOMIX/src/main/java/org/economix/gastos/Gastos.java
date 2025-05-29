package org.economix.gastos;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;
import org.economix.usuario.Usuario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@AttributeOverride(
        name = "id",
        column = @Column(name = "idGastos")
)
@Entity
@Table(name = "tbl_gastos")                       // ← igual que en la BD
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Gastos extends Catalogo implements Serializable{

    @ToString.Include(name = "idUsuario")        // ★
    public Integer getIdUsuario() {
        return usuario != null ? usuario.getId() : null;
    }

    @ToString.Include
    @Column(name = "artículoGasto", length = 100)      // ← default: nullable = true
    private String articuloGasto;

    @ToString.Include
    @Column(name = "descripciónGasto", columnDefinition = "TEXT")
    private String descripcionGastos;

    @ToString.Include
    @Column(name = "montoGasto", precision = 10, scale = 2)
    private BigDecimal montoGastos;

    @ToString.Include
    @Column(name = "fechaGastos")
    private Date fechaGastos;

    @ToString.Include
    @Column(name = "periodoGastos", length = 50)
    private String periodoGastos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    @OneToMany(mappedBy = "gastos", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude               // evita colección pesada
    private List<conceptoGastos> conceptoGastos = new ArrayList<>();
}
