package org.economix.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;
import org.economix.vista.acciones.Ejecutable;

import java.io.Serializable;

@AttributeOverride(
        name = "id",
        column = @Column(name = "idContactos")
)
@Entity
@Table(name = "tbl_contactos")                       // ← igual que en la BD
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Contacto extends Catalogo implements Serializable
{
    @ToString.Include(name = "idUsuario")        // ★
    public Integer getIdUsuario() {
        return usuario != null ? usuario.getId() : null;
    }

    @ToString.Include
    @Column(name = "numCelular", nullable = false, length = 20)
    private String numCelular;

    @ToString.Include
    @Column(name = "Correo", nullable = false, length = 100)
    private String correo;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;
}
