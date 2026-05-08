package com.GenMan.GenMan.DTO.Relaciones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrimaProveedorCantidadDTO {
    private Long materiaPrimaId;
    private String nombre;
    private String asset;
    private String unidad;
    private Double cantidadDisponible;
    private String marca;
    private Double precio;
}
