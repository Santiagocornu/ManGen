package com.GenMan.GenMan.Entities;


import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Pedidos;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "pedidos", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Producto_Pedidos> productos = new ArrayList<>();
}
