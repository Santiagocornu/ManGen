package com.GenMan.GenMan.Entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Pedidos")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Pedidos {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long Id;

    @Column
    Double Total;

    @Column
    Double TotalDesc;

    @Column
    String Descripcion;

    @Column
    String Estado;
}
