package org.economix.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.economix.model.Catalogo;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "DOMICILIO")
public class Domicilio extends Catalogo implements Serializable {

    @Column(name = "domicilio", nullable = false, length = 150)
    private String domicilio;
}
