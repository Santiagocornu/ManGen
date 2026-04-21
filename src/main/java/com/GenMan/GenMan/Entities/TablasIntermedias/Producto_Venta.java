package com.GenMan.GenMan.Entities.TablasIntermedias;

import com.GenMan.GenMan.Entities.Producto;
import com.GenMan.GenMan.Entities.Ventas;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Entity
@Table(name = "Producto_Venta")
@IdClass(Producto_Venta.Id.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto_Venta {

    @jakarta.persistence.Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Venta_id", nullable = false)
    private Ventas venta;

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
        private Long venta;
        private Long producto;
    }
}
