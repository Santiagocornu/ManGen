package com.GenMan.GenMan.DTO.TablasIntermedias;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrimaProductosDTO {
    private Long materiaPrimaId;
    private Long productoId;
    private Double cantidad;
}
