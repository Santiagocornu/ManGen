package com.GenMan.GenMan.Controller;

import com.GenMan.GenMan.DTO.LoginRequestDTO;
import com.GenMan.GenMan.DTO.LoginResponseDTO;
import com.GenMan.GenMan.DTO.PagarSucursalRequestDTO;
import com.GenMan.GenMan.DTO.RegisterSucursalAdminRequestDTO;
import com.GenMan.GenMan.DTO.RegisterSucursalAdminResponseDTO;
import com.GenMan.GenMan.DTO.SucursalDTO;
import com.GenMan.GenMan.DTO.UserDTO;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Security.JwtService;
import com.GenMan.GenMan.Service.SucursalService;
import com.GenMan.GenMan.Service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;
    private final SucursalService sucursalService;
    private final String paymentKey;

    public AuthController(JwtService jwtService,
                          UserService userService,
                          SucursalService sucursalService,
                          @Value("${Key}") String paymentKey) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.sucursalService = sucursalService;
        this.paymentKey = paymentKey;
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

    @PostMapping("/pagar-sucursal")
    public SucursalDTO pagarSucursal(@RequestBody PagarSucursalRequestDTO requestDTO) {
        if (requestDTO.getSucursalId() == null) {
            throw new BadRequestException("El id de la sucursal es obligatorio");
        }
        if (requestDTO.getKey() == null || !requestDTO.getKey().equals(paymentKey)) {
            throw new BadRequestException("Key invalida");
        }

        return sucursalService.marcarComoPago(requestDTO.getSucursalId());
    }

    @PostMapping("/desactivar-pago-sucursal")
    public SucursalDTO desactivarPagoSucursal(@RequestBody PagarSucursalRequestDTO requestDTO) {
        if (requestDTO.getSucursalId() == null) {
            throw new BadRequestException("El id de la sucursal es obligatorio");
        }
        if (requestDTO.getKey() == null || !requestDTO.getKey().equals(paymentKey)) {
            throw new BadRequestException("Key invalida");
        }

        return sucursalService.marcarComoNoPago(requestDTO.getSucursalId());
    }

    @GetMapping("/sucursales")
    public List<SucursalDTO> getAllSucursales(@RequestParam String key) {
        if (key == null || !key.equals(paymentKey)) {
            throw new BadRequestException("Key invalida");
        }

        return sucursalService.findAllForGestion();
    }

    @PostMapping("/change-password")
    public UserDTO changePassword(@RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String newPassword) {
        return userService.cambiarContrasena(email, password, newPassword);
    }
}
