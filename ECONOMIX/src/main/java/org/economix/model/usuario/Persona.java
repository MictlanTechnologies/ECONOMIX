package org.economix.model.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@AttributeOverride(
        name = "id",
        column = @Column(name = "idPersona")
)
@Entity
@Table(name = "tbl_persona")                       // ← igual que en la BD
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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

    @OneToMany(mappedBy = "persona",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Domicilio> domicilios = new ArrayList<>();

    @OneToMany(mappedBy = "persona",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Contacto> contactos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    @Override
    public String toString() {
        return nombreP + " " + apellidoP + " " + apellidoM;
    }
}