package org.economix.model.presupuesto;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.usuario.Usuario;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "tbl_presupuesto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Presupuesto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPresupuesto;

    @Column(nullable = false, length = 40)
    private String categoria;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoMaximo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoGastado = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;
}