package org.economix.gastos;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;
import org.economix.usuario.Usuario;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.type.descriptor.jdbc.NumericJdbcType;
import org.w3c.dom.Text;

import java.awt.*;
import java.io.Serializable;
import java.sql.Date;

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
@Table( name="TBL_GASTOS" ) // Le dice a Hibernate a qué tabla de la BD refiere

public class Gastos extends Catalogo implements Serializable
{
    @Column(name = "artículoGasto", nullable = false )
    private String gastos;

    @Column( name = "descriocionGastos", nullable = false)
    private Text descripcionGastos;

    @Column( name = "montoGastos", nullable = false)
    private NumericJdbcType montoGastos;

    @Column( name = "fechaGastos", nullable = false)
    private Date fechaGastos;

    @Column( name = "periodoGastos", nullable = false )
    private String periodoGastos;

    @ManyToOne
    @JoinColumn( name = "TBL_USUARiO_idUsuario")
    private Usuario usuario;

    public static Ejecutable getInstance() {
        return null;
    }
}
