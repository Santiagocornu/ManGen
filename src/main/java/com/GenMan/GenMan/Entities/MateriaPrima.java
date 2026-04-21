package com.GenMan.GenMan.Entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "MateriaPrima")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class MateriaPrima {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long Id;

    @Column()
    String Asset;

    @Column()
    String Nombre;

    @Column(nullable = false)
    Double Precio;

    @Column(nullable = false)
    String Unidad;

    @Column(nullable = false)
    Double Cantidad;

}
