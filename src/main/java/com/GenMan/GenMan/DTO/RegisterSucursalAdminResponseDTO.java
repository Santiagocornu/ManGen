package com.GenMan.GenMan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterSucursalAdminResponseDTO {
    private String token;
    private SucursalDTO sucursal;
    private UserDTO user;
}
