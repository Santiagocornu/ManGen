package com.GenMan.GenMan.DTO.TablasIntermedias;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrimaProveedorDTO {
    private Long materiaPrimaId;
    private Long proveedorId;
    private String marca;
    private Double precio;
}
