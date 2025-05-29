package org.economix.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;
import org.economix.vista.acciones.Ejecutable;


import java.io.Serializable;


@AttributeOverride(
        name = "id",
        column = @Column(name = "idPersona")
)
@Entity
@Table(name = "tbl_persona")                       // ← igual que en la BD
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Persona extends Catalogo implements Serializable{

    @ToString.Include(name = "idUsuario")        // ★
    public Integer getIdUsuario() {
        return usuario != null ? usuario.getId() : null;
    }

    @ToString.Include
    @Column(name = "nombrePersona", nullable = false )
    private String nombreP;

    @ToString.Include
    @Column(name = "apellidoP", nullable = false )
    private String apellidoP;

    @ToString.Include
    @Column(name = "apellidoM", nullable = false )
    private String apellidoM;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    @ToString.Exclude
    private Usuario usuario;
}