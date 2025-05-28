package org.economix.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.economix.gastos.Gastos;
import org.economix.model.Catalogo;
import org.economix.vista.acciones.Ejecutable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AttributeOverride(
        name = "id",
        column = @Column(name = "idUsuario")
)
@Entity
@Table(name = "tbl_usuario")                       // ← igual que en la BD
@Data @NoArgsConstructor @AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Usuario extends Catalogo implements Serializable{


    @ToString.Include
    @Column(name = "perfilUsuario", length = 100)
    private String perfilUsuario;

    @ToString.Include
    @Column(name = "contraseñaUsuario", length = 100)
    private String contraseñaUsuario;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude               // evita colección pesada
    private List<Gastos> gastos = new ArrayList<>();
    public static Ejecutable getInstance() {
        return null;
    }
}
