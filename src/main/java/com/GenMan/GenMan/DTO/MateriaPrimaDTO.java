package com.GenMan.GenMan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrimaDTO {
    private Long id;
    private String asset;
    private String nombre;
    private Double precio;
    private String unidad;
    private Double cantidad;
}
