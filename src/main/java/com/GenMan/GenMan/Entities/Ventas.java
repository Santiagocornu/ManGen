package com.GenMan.GenMan.Entities;


import jakarta.persistence.*;
import lombok.*;

import java.text.DateFormat;

@Entity
@Table(name = "Ventas")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Ventas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long Id;

    @Column(nullable = false)
    double Total;

    @Column
    double TotalDesc;

    @Column
    String MetodoPago;

    @Column
    DateFormat Fecha;
}
