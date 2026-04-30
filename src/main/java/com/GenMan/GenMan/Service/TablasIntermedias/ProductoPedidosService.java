package com.GenMan.GenMan.Service.TablasIntermedias;

import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.ProductoPedidosDTO;
import com.GenMan.GenMan.Entities.Pedidos;
import com.GenMan.GenMan.Entities.Producto;
import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Pedidos;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.PedidosRepository;
import com.GenMan.GenMan.Repository.ProductoRepository;
import com.GenMan.GenMan.Repository.TablasIntermedias.Producto_PedidosRepository;
import com.GenMan.GenMan.Security.SucursalContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductoPedidosService {

    private final Producto_PedidosRepository productoPedidosRepository;
    private final PedidosRepository pedidosRepository;
    private final ProductoRepository productoRepository;

    public ProductoPedidosService(
            Producto_PedidosRepository productoPedidosRepository,
            PedidosRepository pedidosRepository,
            ProductoRepository productoRepository
    ) {
        this.productoPedidosRepository = productoPedidosRepository;
        this.pedidosRepository = pedidosRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public ProductoPedidosDTO crearOActualizar(Long pedidoId, Long productoId, Integer cantidad) {
        validateCantidad(cantidad);

        Pedidos pedido = pedidosRepository.findByIdAndSucursal_Id(pedidoId, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + pedidoId));

        Producto producto = productoRepository.findByIdAndSucursal_Id(productoId, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productoId));

        Producto_Pedidos relacion = productoPedidosRepository
                .findByPedidos_IdAndProducto_Id(pedidoId, productoId)
                .orElse(new Producto_Pedidos(pedido, producto, cantidad));

        relacion.setCantidad(cantidad);
        return toDto(productoPedidosRepository.save(relacion));
    }

    @Transactional
    public ProductoPedidosDTO cambiarCantidad(Long pedidoId, Long productoId, Integer cantidad) {
        validateCantidad(cantidad);

        Producto_Pedidos relacion = productoPedidosRepository.findByPedidos_IdAndProducto_Id(pedidoId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe relacion entre pedido " + pedidoId + " y producto " + productoId));

        validateSameSucursal(relacion.getPedidos().getSucursal() == null ? null : relacion.getPedidos().getSucursal().getId());
        validateSameSucursal(relacion.getProducto().getSucursal() == null ? null : relacion.getProducto().getSucursal().getId());

        relacion.setCantidad(cantidad);
        return toDto(productoPedidosRepository.save(relacion));
    }

    public List<ProductoCantidadDTO> obtenerProductosPorPedido(Long pedidoId) {
        if (!pedidosRepository.existsByIdAndSucursal_Id(pedidoId, currentSucursalId())) {
            throw new ResourceNotFoundException("Pedido no encontrado con id: " + pedidoId);
        }

        return productoPedidosRepository.findAllByPedidos_Id(pedidoId)
                .stream()
                .map(relacion -> new ProductoCantidadDTO(
                        relacion.getProducto().getId(),
                        relacion.getProducto().getNombre(),
                        relacion.getProducto().getAsset(),
                        relacion.getProducto().getPrecio(),
                        relacion.getProducto().getUnidad(),
                        relacion.getProducto().getCantidad(),
                        relacion.getCantidad()
                ))
                .toList();
    }

    private void validateCantidad(Integer cantidad) {
        if (cantidad == null) {
            throw new BadRequestException("La cantidad no puede ser nula");
        }
        if (cantidad < 0) {
            throw new BadRequestException("La cantidad debe ser mayor o igual a 0");
        }
    }

    private Long currentSucursalId() {
        Long sucursalId = SucursalContext.get();
        if (sucursalId == null) {
            throw new BadRequestException("No se pudo resolver la sucursal actual");
        }
        return sucursalId;
    }

    private void validateSameSucursal(Long sucursalId) {
        if (sucursalId == null || !sucursalId.equals(currentSucursalId())) {
            throw new ResourceNotFoundException("La relacion no pertenece a la sucursal actual");
        }
    }

    private ProductoPedidosDTO toDto(Producto_Pedidos relacion) {
        return new ProductoPedidosDTO(
                relacion.getPedidos().getId(),
                relacion.getProducto().getId(),
                relacion.getCantidad()
        );
    }
}
