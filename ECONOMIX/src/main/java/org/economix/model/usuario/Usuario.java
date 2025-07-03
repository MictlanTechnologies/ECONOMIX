// Paquete que contiene las entidades relacionadas con los usuarios del sistema.
package org.economix.model.usuario;

/**
 * Entidad que representa un usuario del sistema ECONOMIX.
 * Contiene credenciales y relaciones con personas y gastos asociados.
 * Hereda el campo `id` de {@link Catalogo}, que se mapea como `idUsuario`.
 */

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.gastos.Gastos;
import org.economix.model.Catalogo;
import org.economix.vista.acciones.Ejecutable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un usuario del sistema ECONOMIX.
 * Contiene credenciales y relaciones con personas y gastos asociados.
 * Hereda el campo `id` de {@link Catalogo}, que se mapea como `idUsuario`.
 */
@AttributeOverride(
        name = "id",
        column = @Column(name = "idUsuario")
)
@Entity
@Table(name = "tbl_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Usuario extends Catalogo implements Serializable {

    /** Nombre de usuario o identificador de perfil */
    @ToString.Include
    private String perfilUsuario;

    /** Contraseña del usuario (se recomienda enmascarar o cifrar en aplicaciones reales) */
    @ToString.Include
    private String contraseñaUsuario;

    /** Lista de gastos registrados por este usuario */
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Gastos> gastos = new ArrayList<>();

    /** Lista de personas asociadas a este usuario */
    @OneToMany(mappedBy = "usuario",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Persona> personas = new ArrayList<>();

    /**
     * Método placeholder para futura implementación del patrón Singleton/Ejecutable.
     * Actualmente retorna null.
     * @return instancia ejecutable nula.
     */
    public static Ejecutable getInstance() {
        return null;
    }
}
