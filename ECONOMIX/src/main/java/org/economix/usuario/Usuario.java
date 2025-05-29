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
@ToString(callSuper = true, onlyExplicitlyIncluded = true)   // ← llamamos al super
public class Usuario extends Catalogo implements Serializable {

    @ToString.Include
    private String perfilUsuario;

    @ToString.Include
    private String contraseñaUsuario;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude               // evita colección pesada
    private List<Gastos> gastos = new ArrayList<>();
    private List<Persona> personas = new ArrayList<>();
    private List<Domicilio> domicilios = new ArrayList<>();
    private List<Contacto> contactos = new ArrayList<>();
    public static Ejecutable getInstance() {
        return null;
    }
}
