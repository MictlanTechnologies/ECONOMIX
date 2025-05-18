package org.economix.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;
import org.economix.vista.acciones.Ejecutable;

import java.io.Serializable;

@AttributeOverride(
        name = "id",
        column = @Column(name = "idUsuario")
)
@Entity
@Table(name = "tbl_usuario")                       // ← igual que en la BD
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
public class Usuario extends Catalogo implements Serializable
{
    @Column(name = "perfilUsuario", length = 100)
    private String perfilUsuario;

    @Column(name = "contraseñaUsuario", length = 100)
    private String contraseñaUsuario;

    public static Ejecutable getInstance() {
        return null;
    }
}