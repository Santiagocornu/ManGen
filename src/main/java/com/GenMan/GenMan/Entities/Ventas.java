package com.GenMan.GenMan.Entities;


import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Venta;
import jakarta.persistence.*;
import lombok.*;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Producto_Venta> productos = new ArrayList<>();
}
