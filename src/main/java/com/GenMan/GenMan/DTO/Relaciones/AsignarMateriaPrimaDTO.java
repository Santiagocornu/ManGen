package com.GenMan.GenMan.DTO.Relaciones;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarMateriaPrimaDTO {
    @JsonAlias({"id", "materia_prima_id"})
    private Long materiaPrimaId;
    private Double cantidad;
}
