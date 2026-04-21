package com.GenMan.GenMan.Entities.TablasIntermedias;

import com.GenMan.GenMan.Entities.MateriaPrima;
import com.GenMan.GenMan.Entities.Producto;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@Entity
@Table(name = "materia_prima_productos")
@IdClass(MateriaPrima_Productos.Id.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrima_Productos {

    @jakarta.persistence.Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_prima_id", nullable = false)
    private MateriaPrima materiaPrima;

    @jakarta.persistence.Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    // Clase para clave compuesta PK
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Id implements Serializable {
        private Long materiaPrima;
        private Long producto;
    }
}