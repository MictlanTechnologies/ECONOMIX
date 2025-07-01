// Paquete que contiene las entidades relacionadas con los usuarios.
package org.economix.model.usuario;

// Importación de anotaciones JPA y utilidades Lombok para la generación de código.
import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;

import java.io.Serializable;

/**
 * Entidad que representa un domicilio en el sistema ECONOMIX.
 * Está mapeada a la tabla `tbl_domicilio` en la base de datos.
 * Hereda el campo `id` de {@link Catalogo}.
 */
@AttributeOverride(
        name = "id",
        column = @Column(name = "idDomicilio")
)
@Entity
@Table(name = "tbl_domicilio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Domicilio extends Catalogo implements Serializable {

    /** Devuelve el ID de la persona asociada (se muestra en el toString si es necesario) */
    @ToString.Include(name = "idPersona")
    public Integer getIdPersona() {
        return persona != null ? persona.getId() : null;
    }

    /** Ciudad donde vive la persona */
    @ToString.Include
    @Column(name = "ciudad", nullable = false, length = 50)
    private String ciudad;

    /** Calle del domicilio */
    @ToString.Include
    @Column(name = "calle", nullable = false, length = 100)
    private String calle;

    /** Colonia del domicilio */
    @ToString.Include
    @Column(name = "colonia", nullable = false, length = 100)
    private String colonia;

    /** Número de la vivienda */
    @ToString.Include
    @Column(name = "número", nullable = false, length = 10)
    private String numero;

    /** Persona asociada al domicilio */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idPersona", nullable = false)
    @ToString.Exclude
    private Persona persona;
}
