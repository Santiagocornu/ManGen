package com.GenMan.GenMan.DTO.Relaciones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarProductoDTO {
    private Long productoId;
    private Integer cantidad;
}
