package org.economix.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Catalogo implements Serializable {
    /** NO pongas @Column aquí; el nombre se define en cada entidad */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}



