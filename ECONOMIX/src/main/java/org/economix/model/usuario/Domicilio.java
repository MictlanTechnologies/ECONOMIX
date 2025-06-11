package org.economix.model.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;

import java.io.Serializable;

@AttributeOverride(
        name = "id",
        column = @Column(name = "idDomicilio")
)
@Entity
@Table(name = "tbl_domicilio")                       // ← igual que en la BD
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Domicilio extends Catalogo implements Serializable{

    @ToString.Include(name = "idPersona")        // ★
    public Integer getIdPersona() {
        return persona != null ? persona.getId() : null;
    }

    @ToString.Include
    @Column(name = "ciudad", nullable = false, length = 50)
    private String ciudad;

    @ToString.Include
    @Column(name = "calle", nullable = false, length = 100)
    private String calle;

    @ToString.Include
    @Column(name = "colonia", nullable = false, length = 100)
    private String colonia;

    @ToString.Include
    @Column(name = "número", nullable = false, length = 10)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idPersona", nullable = false)
    @ToString.Exclude
    private Persona persona;
}
