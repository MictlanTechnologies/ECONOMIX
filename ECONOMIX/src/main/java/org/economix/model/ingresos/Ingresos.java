package org.economix.model.ingresos;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;
import org.economix.model.usuario.Usuario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@AttributeOverride(
        name = "id",
        column = @Column(name = "idIngresos")
)
@Entity
@Table(name = "tbl_ingresos")                       // ← igual que en la BD
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Ingresos extends Catalogo implements Serializable {

    @ToString.Include(name = "idUsuario")        // ★
    public Integer getIdUsuario() {
        return usuario != null ? usuario.getId() : null;
    }

    @ToString.Include
    @Column(name = "montoIngreso", precision = 10, scale = 2)
    private BigDecimal montoIngreso;

    @ToString.Include
    @Column(name = "periodicidadIngreso ", length = 50)
    private String periodicidadIngreso;

    @ToString.Include
    @Column(name = "fechaIngresos")
    private Date fechaIngresos;

    @ToString.Include
        @Column(name = "descripcionIngreso", columnDefinition = "TEXT")
    private String descripcionIngreso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    @OneToMany(mappedBy = "ingresos", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude               // evita colección pesada
    private List<conceptoIngresos> conceptoIngresos = new ArrayList<>();
}
