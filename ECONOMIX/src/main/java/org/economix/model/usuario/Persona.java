// Paquete que contiene las entidades relacionadas con los usuarios.
package org.economix.model.usuario;

// Importación de anotaciones de JPA y utilidades de Lombok.
import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a una persona dentro del sistema ECONOMIX.
 * Está vinculada con un usuario y puede tener varios domicilios y contactos.
 * Hereda el campo `id` de {Catalogo}, que se mapea como `idPersona`.
 */
@AttributeOverride(
        name = "id",
        column = @Column(name = "idPersona")
)
@Entity
@Table(name = "tbl_persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Persona extends Catalogo implements Serializable {

    /** Devuelve el ID del usuario vinculado a la persona (útil para debug o trazabilidad) */
    @ToString.Include(name = "idUsuario")
    public Integer getIdUsuario() {
        return usuario != null ? usuario.getId() : null;
    }

    /** Nombre de la persona (obligatorio) */
    @ToString.Include
    @Column(name = "nombrePersona", nullable = false)
    private String nombreP;

    /** Apellido paterno de la persona (obligatorio) */
    @ToString.Include
    @Column(name = "apellidoP", nullable = false)
    private String apellidoP;

    /** Apellido materno de la persona (obligatorio) */
    @ToString.Include
    @Column(name = "apellidoM", nullable = false)
    private String apellidoM;

    /** Lista de domicilios vinculados a la persona */
    @OneToMany(mappedBy = "persona",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Domicilio> domicilios = new ArrayList<>();

    /** Lista de contactos vinculados a la persona */
    @OneToMany(mappedBy = "persona",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Contacto> contactos = new ArrayList<>();

    /** Usuario al que está asociada la persona */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    /**
     * Retorna el nombre completo de la persona.
     * @return String con nombre y apellidos concatenados.
     */
    @Override
    public String toString() {
        return nombreP + " " + apellidoP + " " + apellidoM;
    }
}
