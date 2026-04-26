package com.GenMan.GenMan.Service;

import com.GenMan.GenMan.DTO.PedidosDTO;
import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.ProductoPedidosDTO;
import com.GenMan.GenMan.Entities.Pedidos;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.PedidosRepository;
import com.GenMan.GenMan.Service.TablasIntermedias.ProductoPedidosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidosService {

    private final PedidosRepository pedidosRepository;
    private final ProductoPedidosService productoPedidosService;

    @Autowired
    public PedidosService(PedidosRepository pedidosRepository, ProductoPedidosService productoPedidosService) {
        this.pedidosRepository = pedidosRepository;
        this.productoPedidosService = productoPedidosService;
    }

    public List<PedidosDTO> findAll() {
        return pedidosRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public PedidosDTO findById(Long id) {
        return pedidosRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con codigo: " + id));
    }

    public PedidosDTO save(PedidosDTO pedidosDTO) {
        validate(pedidosDTO);
        Pedidos pedidos = toEntity(pedidosDTO);
        return toDto(pedidosRepository.save(pedidos));
    }

    public PedidosDTO update(Long id, PedidosDTO pedidosDTO) {
        validate(pedidosDTO);

        Pedidos pedidoExistente = pedidosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro pedido con id: " + id));

        pedidoExistente.setTotal(pedidosDTO.getTotal());
        pedidoExistente.setTotalDesc(pedidosDTO.getTotalDesc());
        pedidoExistente.setDescripcion(pedidosDTO.getDescripcion());
        pedidoExistente.setEstado(pedidosDTO.getEstado());

        return toDto(pedidosRepository.save(pedidoExistente));
    }

    public void deleteById(Long id) {
        if (!pedidosRepository.existsById(id)) {
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
}
