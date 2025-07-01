// Paquete que contiene las entidades relacionadas con los usuarios.
package org.economix.model.usuario;

// Importación de librerías JPA y utilidades de Lombok.
import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;

import java.io.Serializable;

/**
 * Entidad que representa los datos de contacto de una persona.
 * Está vinculada a la tabla `tbl_contactos` de la base de datos.
 * Hereda el campo `id` desde {@link Catalogo}.
 */
@AttributeOverride(
        name = "id",
        column = @Column(name = "idContactos")
)
@Entity
@Table(name = "tbl_contactos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Contacto extends Catalogo implements Serializable {

    /** Devuelve el ID de la persona asociada al contacto (usado en @ToString) */
    @ToString.Include(name = "idPersona")
    public Integer getIdUsuario() {
        return persona != null ? persona.getId() : null;
    }

    /** Número de celular del contacto */
    @ToString.Include
    @Column(name = "numCelular", nullable = false, length = 20)
    private String numCelular;

    /** Correo electrónico del contacto */
    @ToString.Include
    @Column(name = "Correo", nullable = false, length = 100)
    private String correo;

    /** Relación muchos-a-uno con la persona a la que pertenece el contacto */
    @ManyToOne
    @JoinColumn(name = "idPersona", nullable = false)
    private Persona persona;
}
