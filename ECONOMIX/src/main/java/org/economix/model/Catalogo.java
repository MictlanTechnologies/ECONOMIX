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

public abstract class Catalogo implements Serializable
{
    @Id
    @Column (name = "ID")
    @GeneratedValue (strategy = GenerationType.IDENTITY )
    protected Integer id;
}

