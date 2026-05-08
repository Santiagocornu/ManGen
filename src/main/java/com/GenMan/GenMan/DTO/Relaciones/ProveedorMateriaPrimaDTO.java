package com.GenMan.GenMan.DTO.Relaciones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorMateriaPrimaDTO {
    private Long proveedorId;
    private String nombre;
    private String email;
    private String numeroTelefono;
    private String descripcion;
    private String marca;
    private Double precio;
}
