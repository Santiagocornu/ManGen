package com.GenMan.GenMan.Service;

import com.GenMan.GenMan.DTO.BackfillResultDTO;
import com.GenMan.GenMan.DTO.RegisterSucursalAdminRequestDTO;
import com.GenMan.GenMan.DTO.SucursalDTO;
import com.GenMan.GenMan.DTO.UserDTO;
import com.GenMan.GenMan.Entities.MateriaPrima;
import com.GenMan.GenMan.Entities.Pedidos;
import com.GenMan.GenMan.Entities.Producto;
import com.GenMan.GenMan.Entities.Sucursal;
import com.GenMan.GenMan.Entities.User;
import com.GenMan.GenMan.Entities.Ventas;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.MateriaPrimaRepository;
import com.GenMan.GenMan.Repository.PedidosRepository;
import com.GenMan.GenMan.Repository.ProductoRepository;
import com.GenMan.GenMan.Repository.SucursalRepository;
import com.GenMan.GenMan.Repository.UserRepository;
import com.GenMan.GenMan.Repository.VentasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Date;

@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final UserRepository userRepository;
    private final MateriaPrimaRepository materiaPrimaRepository;
    private final ProductoRepository productoRepository;
    private final PedidosRepository pedidosRepository;
    private final VentasRepository ventasRepository;

    public SucursalService(
            SucursalRepository sucursalRepository,
            UserRepository userRepository,
            MateriaPrimaRepository materiaPrimaRepository,
            ProductoRepository productoRepository,
            PedidosRepository pedidosRepository,
            VentasRepository ventasRepository
    ) {
        this.sucursalRepository = sucursalRepository;
        this.userRepository = userRepository;
        this.materiaPrimaRepository = materiaPrimaRepository;
        this.productoRepository = productoRepository;
        this.pedidosRepository = pedidosRepository;
        this.ventasRepository = ventasRepository;
    }

    public List<SucursalDTO> findAll() {
        return sucursalRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public SucursalDTO findById(Long id) {
        return sucursalRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
    }

    public SucursalDTO save(SucursalDTO sucursalDTO) {
        validateNombre(sucursalDTO.getNombre(), null);

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(sucursalDTO.getNombre());
        return toDto(sucursalRepository.save(sucursal));
    }

    @Transactional
    public RegisterSucursalAdminData registerSucursalWithAdmin(RegisterSucursalAdminRequestDTO requestDTO) {
        validateNombre(requestDTO.getNombreSucursal(), null);
        validateAdminRequest(requestDTO);

        userRepository.findByEmail(requestDTO.getEmailAdmin())
                .ifPresent(user -> {
                    throw new BadRequestException("Ya existe un usuario con ese email");
                });

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(requestDTO.getNombreSucursal());
        Sucursal sucursalGuardada = sucursalRepository.save(sucursal);

        User admin = new User();
        admin.setNombre(requestDTO.getNombreAdmin());
        admin.setFechaCreacion(new Date());
        admin.setEmail(requestDTO.getEmailAdmin());
        admin.setPassword(requestDTO.getPasswordAdmin());
        admin.setRoll("ADMIN");
        admin.setSucursal(sucursalGuardada);

        User adminGuardado = userRepository.save(admin);

        return new RegisterSucursalAdminData(toDto(sucursalGuardada), toDto(adminGuardado));
    }

    public SucursalDTO update(Long id, SucursalDTO sucursalDTO) {
        validateNombre(sucursalDTO.getNombre(), id);

        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));

        sucursal.setNombre(sucursalDTO.getNombre());
        return toDto(sucursalRepository.save(sucursal));
    }

    public void deleteById(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));

        if (!userRepository.findAllBySucursal_Id(id).isEmpty()
                || !materiaPrimaRepository.findAllBySucursal_Id(id).isEmpty()
                || !productoRepository.findAllBySucursal_Id(id).isEmpty()
                || !pedidosRepository.findAllBySucursal_Id(id).isEmpty()
                || !ventasRepository.findAllBySucursal_Id(id).isEmpty()) {
            throw new BadRequestException("No se puede eliminar una sucursal que todavia tiene datos asociados");
        }

        sucursalRepository.delete(sucursal);
    }

    @Transactional
    public BackfillResultDTO backfillNullData(Long sucursalId) {
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + sucursalId));

        List<User> usuarios = userRepository.findAllBySucursalIsNull();
        usuarios.forEach(user -> user.setSucursal(sucursal));
        userRepository.saveAll(usuarios);

        List<MateriaPrima> materiasPrimas = materiaPrimaRepository.findAllBySucursalIsNull();
        materiasPrimas.forEach(materiaPrima -> materiaPrima.setSucursal(sucursal));
        materiaPrimaRepository.saveAll(materiasPrimas);

        List<Producto> productos = productoRepository.findAllBySucursalIsNull();
        productos.forEach(producto -> producto.setSucursal(sucursal));
        productoRepository.saveAll(productos);

        List<Pedidos> pedidos = pedidosRepository.findAllBySucursalIsNull();
        pedidos.forEach(pedido -> pedido.setSucursal(sucursal));
        pedidosRepository.saveAll(pedidos);

        List<Ventas> ventas = ventasRepository.findAllBySucursalIsNull();
        ventas.forEach(venta -> venta.setSucursal(sucursal));
        ventasRepository.saveAll(ventas);

        return new BackfillResultDTO(
                sucursalId,
                usuarios.size(),
                materiasPrimas.size(),
                productos.size(),
                pedidos.size(),
                ventas.size()
        );
    }

    private void validateNombre(String nombre, Long currentId) {
        if (nombre == null || nombre.isBlank()) {
            throw new BadRequestException("El nombre de la sucursal es obligatorio");
        }

        sucursalRepository.findByNombre(nombre)
                .filter(sucursal -> !sucursal.getId().equals(currentId))
                .ifPresent(sucursal -> {
                    throw new BadRequestException("Ya existe una sucursal con ese nombre");
                });
    }

    private void validateAdminRequest(RegisterSucursalAdminRequestDTO requestDTO) {
        if (requestDTO.getNombreAdmin() == null || requestDTO.getNombreAdmin().isBlank()) {
            throw new BadRequestException("El nombre del admin es obligatorio");
        }
        if (requestDTO.getEmailAdmin() == null || requestDTO.getEmailAdmin().isBlank()) {
            throw new BadRequestException("El email del admin es obligatorio");
        }
        if (requestDTO.getPasswordAdmin() == null || requestDTO.getPasswordAdmin().isBlank()) {
            throw new BadRequestException("La contraseña del admin es obligatoria");
        }
    }

    private SucursalDTO toDto(Sucursal sucursal) {
        return new SucursalDTO(sucursal.getId(), sucursal.getNombre());
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

    public record RegisterSucursalAdminData(SucursalDTO sucursal, UserDTO user) {
    }
}
