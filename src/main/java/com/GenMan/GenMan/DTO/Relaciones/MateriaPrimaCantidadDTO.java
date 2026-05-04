package com.GenMan.GenMan.DTO.Relaciones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrimaCantidadDTO {
    private Long materiaPrimaId;
    private String nombre;
    private String asset;
    private Double precio;
    private String unidad;
    private Double cantidadDisponible;
    private Double cantidadRelacionada;
}
