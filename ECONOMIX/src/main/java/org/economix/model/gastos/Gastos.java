// Paquete que contiene entidades relacionadas con gastos.
package org.economix.model.gastos;

// Importaciones necesarias para la persistencia, uso de Lombok y colecciones.
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
 * Entidad que representa un gasto realizado por un usuario.
 * Se almacena en la tabla `tbl_gastos` y hereda de {@link Catalogo}.
 */
@AttributeOverride(
        name = "id",
        column = @Column(name = "idGastos")  // Renombrar campo heredado
)
@Entity
@Table(name = "tbl_gastos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Gastos extends Catalogo implements Serializable {

    /**
     * Devuelve el ID del usuario que registró el gasto.
     */
    @ToString.Include(name = "idUsuario")
    public Integer getIdUsuario() {
        return usuario != null ? usuario.getId() : null;
    }

    /** Nombre del artículo adquirido */
    @ToString.Include
    @Column(name = "artículoGasto", length = 100)
    private String articuloGasto;

    /** Descripción del gasto */
    @ToString.Include
    @Column(name = "descripciónGasto", columnDefinition = "TEXT")
    private String descripcionGastos;

    /** Monto gastado */
    @ToString.Include
    @Column(name = "montoGasto", precision = 10, scale = 2)
    private BigDecimal montoGastos;

    /** Fecha en la que se realizó el gasto */
    @ToString.Include
    @Column(name = "fechaGastos")
    private Date fechaGastos;

    /** Periodo asociado al gasto (quincenal, mensual, etc.) */
    @ToString.Include
    @Column(name = "periodoGastos", length = 50)
    private String periodoGastos;

    /** Relación muchos-a-uno con Usuario (quién registró el gasto) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    /** Lista de conceptos asociados al gasto (uno-a-muchos) */
    @OneToMany(mappedBy = "gastos", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<conceptoGastos> conceptoGastos = new ArrayList<>();

    /**
     * Muestra el gasto en formato resumen (usado en listados).
     */
    @Override
    public String toString() {
        return articuloGasto + " " + montoGastos + " ";
    }
}
