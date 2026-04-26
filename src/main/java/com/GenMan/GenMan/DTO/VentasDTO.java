package com.GenMan.GenMan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasDTO {
    private Long id;
    private Double total;
    private Double totalDesc;
    private String metodoPago;
    private DateFormat fecha;
}
