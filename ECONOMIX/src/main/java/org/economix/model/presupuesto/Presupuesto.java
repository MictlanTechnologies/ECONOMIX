// Paquete que contiene las entidades relacionadas con presupuestos.
package org.economix.model.presupuesto;

// Importaciones necesarias: JPA, Lombok, tipos de datos y entidades relacionadas.
import jakarta.persistence.*;
import lombok.*;
import org.economix.model.usuario.Usuario;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidad que representa un presupuesto asignado por el usuario para una categoría específica
 * en un mes y año determinados. Se almacena en la tabla `tbl_presupuesto`.
 */
@Entity
@Table(name = "tbl_presupuesto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Presupuesto implements Serializable {

    /** Identificador único del presupuesto (clave primaria) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPresupuesto;

    /** Categoría del presupuesto (ej. Alimentación, Transporte) */
    @Column(nullable = false, length = 40)
    private String categoria;

    /** Monto máximo permitido para gastar en esa categoría */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoMaximo;

    /** Monto que se ha gastado hasta el momento (inicializado en cero) */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoGastado = BigDecimal.ZERO;

    /** Mes al que corresponde el presupuesto (1 = enero, 12 = diciembre) */
    @Column(nullable = false)
    private Integer mes;

    /** Año al que pertenece el presupuesto */
    @Column(nullable = false)
    private Integer anio;

    /** Relación muchos-a-uno con el usuario que definió el presupuesto */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;
}
