package com.GenMan.GenMan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidosDTO {
    private Long id;
    private Double total;
    private Double totalDesc;
    private String descripcion;
    private String estado;
}
