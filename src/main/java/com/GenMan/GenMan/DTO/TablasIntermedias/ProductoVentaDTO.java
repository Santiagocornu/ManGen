package com.GenMan.GenMan.DTO.TablasIntermedias;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoVentaDTO {
    private Long ventaId;
    private Long productoId;
    private Double cantidad;
}
