package com.GenMan.GenMan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterSucursalAdminRequestDTO {
    private String nombreSucursal;
    private String nombreAdmin;
    private String emailAdmin;
    private String passwordAdmin;
}
