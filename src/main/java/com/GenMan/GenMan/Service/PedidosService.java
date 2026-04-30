package com.GenMan.GenMan.Service;

import com.GenMan.GenMan.DTO.PedidosDTO;
import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.ProductoPedidosDTO;
import com.GenMan.GenMan.Entities.Pedidos;
import com.GenMan.GenMan.Entities.Sucursal;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.PedidosRepository;
import com.GenMan.GenMan.Repository.SucursalRepository;
import com.GenMan.GenMan.Security.SucursalContext;
import com.GenMan.GenMan.Service.TablasIntermedias.ProductoPedidosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PedidosService {

    private final PedidosRepository pedidosRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoPedidosService productoPedidosService;

    @Autowired
    public PedidosService(
            PedidosRepository pedidosRepository,
            SucursalRepository sucursalRepository,
            ProductoPedidosService productoPedidosService
    ) {
        this.pedidosRepository = pedidosRepository;
        this.sucursalRepository = sucursalRepository;
        this.productoPedidosService = productoPedidosService;
    }

    public List<PedidosDTO> findAll() {
        return pedidosRepository.findAllBySucursal_Id(currentSucursalId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public PedidosDTO findById(Long id) {
        return pedidosRepository.findByIdAndSucursal_Id(id, currentSucursalId())
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con codigo: " + id));
    }

    @Transactional
    public PedidosDTO save(PedidosDTO pedidosDTO) {
        validate(pedidosDTO);
        Pedidos pedidos = toEntity(pedidosDTO);
        pedidos.setSucursal(currentSucursal());
        return toDto(pedidosRepository.save(pedidos));
    }

    @Transactional
    public PedidosDTO update(Long id, PedidosDTO pedidosDTO) {
        validate(pedidosDTO);

        Pedidos pedidoExistente = pedidosRepository.findByIdAndSucursal_Id(id, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro pedido con id: " + id));

        pedidoExistente.setTotal(pedidosDTO.getTotal());
        pedidoExistente.setTotalDesc(pedidosDTO.getTotalDesc());
        pedidoExistente.setDescripcion(pedidosDTO.getDescripcion());
        pedidoExistente.setEstado(pedidosDTO.getEstado());

        return toDto(pedidosRepository.save(pedidoExistente));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!pedidosRepository.existsByIdAndSucursal_Id(id, currentSucursalId())) {
            throw new ResourceNotFoundException("No se pudo eliminar. Pedido no encontrado con codigo: " + id);
        }
        pedidosRepository.deleteById(id);
    }

    public List<ProductoCantidadDTO> getProductos(Long pedidoId) {
        return productoPedidosService.obtenerProductosPorPedido(pedidoId);
    }

    public ProductoPedidosDTO saveOrUpdateProducto(Long pedidoId, Long productoId, Integer cantidad) {
        return productoPedidosService.crearOActualizar(pedidoId, productoId, cantidad);
    }

    public ProductoPedidosDTO cambiarCantidadProducto(Long pedidoId, Long productoId, Integer cantidad) {
        return productoPedidosService.cambiarCantidad(pedidoId, productoId, cantidad);
    }

    private void validate(PedidosDTO pedidosDTO) {
        if (pedidosDTO.getTotal() != null && pedidosDTO.getTotal() < 0) {
            throw new BadRequestException("El total debe ser mayor o igual a 0");
        }
        if (pedidosDTO.getTotalDesc() != null && pedidosDTO.getTotalDesc() < 0) {
            throw new BadRequestException("El total con descuento debe ser mayor o igual a 0");
        }
    }

    private PedidosDTO toDto(Pedidos pedidos) {
        return new PedidosDTO(
                pedidos.getId(),
                pedidos.getTotal(),
                pedidos.getTotalDesc(),
                pedidos.getDescripcion(),
                pedidos.getEstado()
        );
    }

    private Pedidos toEntity(PedidosDTO pedidosDTO) {
        Pedidos pedidos = new Pedidos();
        pedidos.setId(pedidosDTO.getId());
        pedidos.setTotal(pedidosDTO.getTotal());
        pedidos.setTotalDesc(pedidosDTO.getTotalDesc());
        pedidos.setDescripcion(pedidosDTO.getDescripcion());
        pedidos.setEstado(pedidosDTO.getEstado());
        return pedidos;
    }

    private Long currentSucursalId() {
        Long sucursalId = SucursalContext.get();
        if (sucursalId == null) {
            throw new BadRequestException("No se pudo resolver la sucursal actual");
        }
        return sucursalId;
    }

    private Sucursal currentSucursal() {
        Long sucursalId = currentSucursalId();
        return sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + sucursalId));
    }
}
