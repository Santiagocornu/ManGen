package com.GenMan.GenMan.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasDTO {
    private Long id;
    private Double total;
    private Double totalDesc;
    private String metodoPago;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecha;
}
