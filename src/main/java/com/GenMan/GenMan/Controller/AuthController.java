package com.GenMan.GenMan.Controller;

import com.GenMan.GenMan.DTO.LoginRequestDTO;
import com.GenMan.GenMan.DTO.LoginResponseDTO;
import com.GenMan.GenMan.DTO.RegisterSucursalAdminRequestDTO;
import com.GenMan.GenMan.DTO.RegisterSucursalAdminResponseDTO;
import com.GenMan.GenMan.DTO.UserDTO;
import com.GenMan.GenMan.Security.JwtService;
import com.GenMan.GenMan.Service.SucursalService;
import com.GenMan.GenMan.Service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;
    private final SucursalService sucursalService;

    public AuthController(JwtService jwtService, UserService userService, SucursalService sucursalService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.sucursalService = sucursalService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        UserDTO user = userService.login(
                loginRequestDTO.getEmail(),
                loginRequestDTO.getPassword(),
                loginRequestDTO.getSucursalId()
        );
        String token = jwtService.generateToken(user.getId(), user.getSucursalId(), user.getRoll());

        return new LoginResponseDTO(token, user);
    }

    @PostMapping("/register-sucursal-admin")
    public RegisterSucursalAdminResponseDTO registerSucursalAdmin(
            @RequestBody RegisterSucursalAdminRequestDTO requestDTO
    ) {
        SucursalService.RegisterSucursalAdminData data = sucursalService.registerSucursalWithAdmin(requestDTO);
        String token = jwtService.generateToken(data.user().getId(), data.user().getSucursalId(), data.user().getRoll());
        return new RegisterSucursalAdminResponseDTO(token, data.sucursal(), data.user());
    }

    @PostMapping("/change-password")
    public UserDTO changePassword(@RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String newPassword) {
        return userService.cambiarContrasena(email, password, newPassword);
    }
}
