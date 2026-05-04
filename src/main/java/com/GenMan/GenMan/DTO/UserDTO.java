package com.GenMan.GenMan.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String nombre;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaCreacion;
    private String email;
    private String roll;
    private Long sucursalId;
}
