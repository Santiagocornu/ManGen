package com.GenMan.GenMan.Entities;

import com.GenMan.GenMan.Entities.TablasIntermedias.MateriaPrima_Productos;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @OneToMany(mappedBy = "materiaPrima", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MateriaPrima_Productos> productos = new ArrayList<>();
}
