package com.GenMan.GenMan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String asset;
    private String nombre;
    private Double precio;
    private Double cantidad;
    private String unidad;
}
