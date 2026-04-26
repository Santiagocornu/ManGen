package com.GenMan.GenMan.Entities;

import com.GenMan.GenMan.Entities.TablasIntermedias.MateriaPrima_Productos;
import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Pedidos;
import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Venta;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MateriaPrima_Productos> materiasPrimas = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Producto_Pedidos> pedidos = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Producto_Venta> ventas = new ArrayList<>();
}
