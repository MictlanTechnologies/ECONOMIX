package org.economix.model.ahorro;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;
import org.economix.model.usuario.Usuario;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidad que representa un objetivo de ahorro del usuario.
 * Cada registro almacena la meta a alcanzar y el monto acumulado.
 */
@AttributeOverride(name = "id", column = @Column(name = "idAhorro"))
@Entity
@Table(name = "tbl_ahorro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Ahorro extends Catalogo implements Serializable {

    /** devuelve el ID del usuario asociado */
    @ToString.Include(name = "idUsuario")
    public Integer getIdUsuario() {
        return usuario != null ? usuario.getId() : null;
    }

    @ToString.Include
    @Column(length = 100, nullable = false)
    private String nombreObjetivo;

    @ToString.Include
    @Column(columnDefinition = "TEXT")
    private String descripcionObjetivo;

    @ToString.Include
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal meta;

    @ToString.Include
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal montoAhorrado = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    @Override
    public String toString() {
        return nombreObjetivo + " " + montoAhorrado + "/" + meta;
    }
}