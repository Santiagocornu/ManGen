package com.GenMan.GenMan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackfillResultDTO {
    private Long sucursalId;
    private int usuariosActualizados;
    private int materiasPrimasActualizadas;
    private int productosActualizados;
    private int pedidosActualizados;
    private int ventasActualizadas;
}
