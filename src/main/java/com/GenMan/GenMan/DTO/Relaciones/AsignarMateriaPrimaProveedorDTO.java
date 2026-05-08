package com.GenMan.GenMan.DTO.Relaciones;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarMateriaPrimaProveedorDTO {
    @JsonAlias({"id", "materia_prima_id"})
    private Long materiaPrimaId;
    @JsonAlias({"proveedor_id"})
    private Long proveedorId;
    private String marca;
    private Double precio;
}
