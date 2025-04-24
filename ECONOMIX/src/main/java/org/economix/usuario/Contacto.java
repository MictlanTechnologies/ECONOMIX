package org.economix.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;
import org.economix.vista.acciones.Ejecutable;

import java.io.Serializable;

@Data //Creador de getters y setters
@AllArgsConstructor //Constructor con todos los argumentos
@NoArgsConstructor //Constructor vacío (default)
@EqualsAndHashCode(callSuper = true)
/*
Sirven para comparar objetos de una clase de manera lógica
y para que funcionen bien en estructuras de datos como HashSet, HashMap, etc.
 */
@ToString(callSuper = true) //Creador de ToString
@Entity //Le dice a Hibernate que esto es una entidad
@Table( name="CONTACTOS" ) // Le dice a Hibernate a qué tabla de la BD refiere

public class Contacto extends Catalogo implements Serializable
{
    @Column(name = "CONTACTOS", nullable = false )
    private String contactos;

    public static Ejecutable getInstance() {
        return null;
    }
}