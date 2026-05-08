package com.GenMan.GenMan.Entities.TablasIntermedias;

import com.GenMan.GenMan.Entities.MateriaPrima;
import com.GenMan.GenMan.Entities.Proveedor;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@jakarta.persistence.Entity
@Table(name = "materia_prima_proveedores")
@IdClass(MateriaPrima_Proveedores.Id.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrima_Proveedores {

    @jakarta.persistence.Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_prima_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MateriaPrima materiaPrima;

    @jakarta.persistence.Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Proveedor proveedor;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private Double precio;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Id implements Serializable {
        private Long materiaPrima;
        private Long proveedor;
    }
}
