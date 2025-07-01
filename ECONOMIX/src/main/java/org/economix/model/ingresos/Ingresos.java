// Paquete que contiene entidades relacionadas con ingresos.
package org.economix.model.ingresos;

// Importaciones necesarias: JPA, Lombok, tipos de datos y entidades relacionadas.
import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;
import org.economix.model.usuario.Usuario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un ingreso registrado por el usuario.
 * Se almacena en la tabla `tbl_ingresos`.
 * Contiene datos como monto, periodicidad, fecha y descripción.
 */
@AttributeOverride(
        name = "id",
        column = @Column(name = "idIngresos") // sobrescribe el ID heredado
)
@Entity
@Table(name = "tbl_ingresos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Ingresos extends Catalogo implements Serializable {

    /**
     * Devuelve el ID del usuario que registró el ingreso.
     */
    @ToString.Include(name = "idUsuario")
    public Integer getIdUsuario() {
        return usuario != null ? usuario.getId() : null;
    }

    /** Monto del ingreso */
    @ToString.Include
    @Column(name = "montoIngreso", precision = 10, scale = 2)
    private BigDecimal montoIngreso;

    /** Periodicidad con la que se recibe el ingreso (e.g. semanal, mensual) */
    @ToString.Include
    @Column(name = "periodicidadIngreso", length = 50)
    private String periodicidadIngreso;

    /** Fecha en que se registró el ingreso */
    @ToString.Include
    @Column(name = "fechaIngresos")
    private Date fechaIngresos;

    /** Descripción adicional del ingreso */
    @ToString.Include
    @Column(name = "descripcionIngreso", columnDefinition = "TEXT")
    private String descripcionIngreso;

    /** Relación muchos-a-uno con el usuario que registró el ingreso */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    /** Lista de conceptos asociados a este ingreso */
    @OneToMany(mappedBy = "ingresos", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<conceptoIngresos> conceptoIngresos = new ArrayList<>();

    /**
     * Representación resumida del ingreso (para catálogos o listas).
     */
    @Override
    public String toString() {
        return descripcionIngreso + " " + montoIngreso + " ";
    }
}
