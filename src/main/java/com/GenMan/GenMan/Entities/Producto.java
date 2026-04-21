package com.GenMan.GenMan.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Producto")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Producto {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
Long Id;

@Column
    String Asset;

@Column
    String Nombre;

@Column(nullable = false)
    Double Precio;

@Column(nullable = false)
    Double Cantidad;

@Column
    String Unidad;
}
