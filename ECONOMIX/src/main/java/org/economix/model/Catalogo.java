// Paquete base donde se definen las entidades compartidas del modelo.
package org.economix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Clase base abstracta para todas las entidades del modelo.
 * Contiene únicamente el campo identificador `id`, el cual es heredado
 * por todas las entidades hijas (como Usuario, Gastos, Ingresos, etc.).
 *
 * Esta clase se declara como {@code @MappedSuperclass}, por lo que no
 * se mapea directamente a una tabla, pero su campo `id` es heredado
 * por todas las entidades concretas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Catalogo implements Serializable {

    /**
     * Identificador único de cada entidad.
     * Se define individualmente en cada entidad hija mediante {@code @AttributeOverride}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}