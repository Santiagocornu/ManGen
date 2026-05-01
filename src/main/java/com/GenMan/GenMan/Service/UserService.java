package com.GenMan.GenMan.Service;

import com.GenMan.GenMan.DTO.UserDTO;
import com.GenMan.GenMan.Entities.Sucursal;
import com.GenMan.GenMan.Entities.User;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.SucursalRepository;
import com.GenMan.GenMan.Repository.UserRepository;
import com.GenMan.GenMan.Security.AuthenticatedUser;
import com.GenMan.GenMan.Security.AuthenticatedUserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final SucursalRepository sucursalRepository;

    public UserService(UserRepository userRepository, SucursalRepository sucursalRepository) {
        this.userRepository = userRepository;
        this.sucursalRepository = sucursalRepository;
    }

    public List<UserDTO> findAll() {
        validateUserManagementPermission();
        return userRepository.findAllBySucursal_Id(currentSucursalId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public UserDTO login(String email, String password, Long sucursalId) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("El email es obligatorio");
        }

        if (password == null || password.isBlank()) {
            throw new BadRequestException("La contraseña es obligatoria");
        }

        User user = resolveLoginUser(email, password, sucursalId);

        if (!user.getPassword().equals(password)) {
            throw new BadRequestException("Credenciales invalidas");
        }

        if (user.getSucursal() == null) {
            throw new BadRequestException("El usuario no tiene sucursal asignada");
        }

        return toDto(user);
    }

    @Transactional
    public UserDTO cambiarContrasena(String email, String password, String newPassword) {
        User user = userRepository.findByEmailAndSucursal_Id(email, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        if (!user.getPassword().equals(password)) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new BadRequestException("La nueva contraseña no puede estar vacia");
        }

        if (newPassword.equals(password)) {
            throw new BadRequestException("La nueva contraseña debe ser distinta a la actual");
        }

        user.setPassword(newPassword);
        return toDto(userRepository.save(user));
    }

    public UserDTO findById(Long id) {
        validateUserManagementPermission();
        return userRepository.findByIdAndSucursal_Id(id, currentSucursalId())
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Transactional
    public UserDTO save(User user) {
        validateUserManagementPermission();
        validateManagedUserSucursal(user);
        user.setSucursal(resolveSucursalForManagedUser(user));
        validateUniqueEmailInSucursal(user.getEmail(), user.getSucursal().getId(), null);
        return toDto(userRepository.save(user));
    }

    @Transactional
    public UserDTO update(Long id, User user) {
        validateUserManagementPermission();
        validateManagedUserSucursal(user);
        User userExistente = userRepository.findByIdAndSucursal_Id(id, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        userExistente.setNombre(user.getNombre());
        userExistente.setFechaCreacion(user.getFechaCreacion());
        userExistente.setEmail(user.getEmail());
        userExistente.setPassword(user.getPassword());
        userExistente.setRoll(user.getRoll());
        userExistente.setSucursal(resolveSucursalForManagedUser(user));
        validateUniqueEmailInSucursal(userExistente.getEmail(), userExistente.getSucursal().getId(), userExistente.getId());

        return toDto(userRepository.save(userExistente));
    }

    @Transactional
    public void deleteById(Long id) {
        validateUserManagementPermission();
        User user = userRepository.findByIdAndSucursal_Id(id, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        if (isSucursalCreator(user)) {
            throw new BadRequestException("No se puede eliminar el usuario creador de la sucursal");
        }
        userRepository.delete(user);
    }

    private User resolveLoginUser(String email, String password, Long sucursalId) {
        if (sucursalId != null) {
            return userRepository.findByEmailAndSucursal_Id(email, sucursalId)
                    .orElseThrow(() -> new BadRequestException("Credenciales invalidas"));
        }

        List<User> matchingUsers = userRepository.findAllByEmail(email)
                .stream()
                .filter(user -> user.getPassword().equals(password))
                .toList();

        if (matchingUsers.isEmpty()) {
            throw new BadRequestException("Credenciales invalidas");
        }

        if (matchingUsers.size() > 1) {
            throw new BadRequestException("Hay mas de una sucursal con ese email. Indica sucursalId para iniciar sesion");
        }

        return matchingUsers.get(0);
    }

    private void validateUniqueEmailInSucursal(String email, Long sucursalId, Long currentUserId) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("El email es obligatorio");
        }

        userRepository.findByEmailAndSucursal_Id(email, sucursalId)
                .filter(user -> currentUserId == null || !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    throw new BadRequestException("Ya existe un usuario con ese email en esta sucursal");
                });
    }

    private boolean isSucursalCreator(User user) {
        Sucursal sucursal = sucursalRepository.findById(currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + currentSucursalId()));

        User creador = resolveSucursalCreator(sucursal);
        return creador != null && creador.getId().equals(user.getId());
    }

    private User resolveSucursalCreator(Sucursal sucursal) {
        if (sucursal.getCreador() != null) {
            return sucursal.getCreador();
        }

        User creador = userRepository
                .findFirstBySucursal_IdAndRollIgnoreCaseOrderByFechaCreacionAscIdAsc(sucursal.getId(), "ADMIN")
                .or(() -> userRepository.findFirstBySucursal_IdOrderByFechaCreacionAscIdAsc(sucursal.getId()))
                .orElse(null);

        if (creador != null) {
            sucursal.setCreador(creador);
            sucursalRepository.save(sucursal);
        }

        return creador;
    }

    private void validateUserManagementPermission() {
        AuthenticatedUser authenticatedUser = AuthenticatedUserContext.get();
        if (authenticatedUser == null || !"ADMIN".equalsIgnoreCase(authenticatedUser.role())) {
            throw new BadRequestException("Solo un admin puede administrar usuarios");
        }
    }

    private void validateManagedUserSucursal(User user) {
        AuthenticatedUser authenticatedUser = AuthenticatedUserContext.get();
        Long currentSucursalId = authenticatedUser == null ? null : authenticatedUser.sucursalId();
        Long userSucursalId = user.getSucursal() == null ? null : user.getSucursal().getId();

        if (currentSucursalId == null) {
            throw new BadRequestException("No se pudo resolver la sucursal del usuario autenticado");
        }

        if (userSucursalId != null && !currentSucursalId.equals(userSucursalId)) {
            throw new BadRequestException("No podes cambiar la sucursal asociada del usuario");
        }
    }

    private Sucursal resolveSucursalForManagedUser(User user) {
        Long sucursalId = currentSucursalId();
        return sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + sucursalId));
    }

    private Long currentSucursalId() {
        AuthenticatedUser authenticatedUser = AuthenticatedUserContext.get();
        Long sucursalId = authenticatedUser == null ? null : authenticatedUser.sucursalId();
        if (sucursalId == null) {
            throw new BadRequestException("No se pudo resolver la sucursal del usuario autenticado");
        }
        return sucursalId;
    }

    private UserDTO toDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getNombre(),
                user.getFechaCreacion(),
                user.getEmail(),
                user.getRoll(),
                user.getSucursal() == null ? null : user.getSucursal().getId()
        );
    }
}
