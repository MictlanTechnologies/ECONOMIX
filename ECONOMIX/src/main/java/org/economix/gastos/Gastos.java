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
@EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
public class Gastos extends Catalogo implements Serializable {

    @Column(name = "artículoGasto", length = 100)      // ← default: nullable = true
    private String articuloGasto;

    @Lob
    @Column(name = "descripciónGasto", columnDefinition = "TEXT")
    private String descripcionGastos;

    @Column(name = "montoGasto", precision = 10, scale = 2)
    private BigDecimal montoGastos;

    @Column(name = "fechaGastos")
    private Date fechaGastos;

    @Column(name = "periodoGastos", length = 50)
    private String periodoGastos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    @ToString.Exclude            // 👈 evita que Lombok lo toque
    @EqualsAndHashCode.Exclude   // opcional, por seguridad
    private Usuario usuario;
}
