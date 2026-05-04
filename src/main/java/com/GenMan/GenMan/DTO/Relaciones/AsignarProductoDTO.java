package com.GenMan.GenMan.DTO.Relaciones;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarProductoDTO {
    @JsonAlias({"id", "producto_id"})
    private Long productoId;
    private Double cantidad;
}
