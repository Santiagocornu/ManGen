package com.GenMan.GenMan.Service;

import com.GenMan.GenMan.DTO.VentasDTO;
import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.ProductoVentaDTO;
import com.GenMan.GenMan.Entities.Pedidos;
import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Pedidos;
import com.GenMan.GenMan.Entities.Ventas;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.PedidosRepository;
import com.GenMan.GenMan.Repository.TablasIntermedias.Producto_PedidosRepository;
import com.GenMan.GenMan.Repository.VentasRepository;
import com.GenMan.GenMan.Service.TablasIntermedias.ProductoVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VentasService {

    private final VentasRepository ventasRepository;
    private final PedidosRepository pedidosRepository;
    private final Producto_PedidosRepository productoPedidosRepository;
    private final ProductoVentaService productoVentaService;

    @Autowired
    public VentasService(
            VentasRepository ventasRepository,
            PedidosRepository pedidosRepository,
            Producto_PedidosRepository productoPedidosRepository,
            ProductoVentaService productoVentaService
    ) {
        this.ventasRepository = ventasRepository;
        this.pedidosRepository = pedidosRepository;
        this.productoPedidosRepository = productoPedidosRepository;
        this.productoVentaService = productoVentaService;
    }

    public List<VentasDTO> findAll() {
        return ventasRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public VentasDTO findById(Long id) {
        return ventasRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con codigo: " + id));
    }

    public VentasDTO save(VentasDTO ventasDTO) {
        validate(ventasDTO);
        Ventas venta = toEntity(ventasDTO);
        return toDto(ventasRepository.save(venta));
    }

    public VentasDTO update(Long id, VentasDTO ventasDTO) {
        validate(ventasDTO);

        Ventas ventaExistente = ventasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro venta con id: " + id));

        ventaExistente.setTotal(ventasDTO.getTotal());
        ventaExistente.setTotalDesc(ventasDTO.getTotalDesc() == null ? 0D : ventasDTO.getTotalDesc());
        ventaExistente.setMetodoPago(ventasDTO.getMetodoPago());
        ventaExistente.setFecha(ventasDTO.getFecha());

        return toDto(ventasRepository.save(ventaExistente));
    }

    public void deleteById(Long id) {
        if (!ventasRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se pudo eliminar. Venta no encontrada con codigo: " + id);
        }
        ventasRepository.deleteById(id);
    }

    public List<ProductoCantidadDTO> getProductos(Long ventaId) {
        return productoVentaService.obtenerProductosPorVenta(ventaId);
    }

    public ProductoVentaDTO saveOrUpdateProducto(Long ventaId, Long productoId, Integer cantidad) {
        return productoVentaService.crearOActualizar(ventaId, productoId, cantidad);
    }

    public ProductoVentaDTO saveOrUpdateProductoConStock(Long ventaId, Long productoId, Integer cantidad) {
        return productoVentaService.crearOActualizarConStock(ventaId, productoId, cantidad);
    }

    public ProductoVentaDTO cambiarCantidadProducto(Long ventaId, Long productoId, Integer cantidad) {
        return productoVentaService.cambiarCantidad(ventaId, productoId, cantidad);
    }

    public ProductoVentaDTO cambiarCantidadProductoConStock(Long ventaId, Long productoId, Integer cantidad) {
        return productoVentaService.cambiarCantidadConStock(ventaId, productoId, cantidad);
    }

    public void eliminarProducto(Long ventaId, Long productoId) {
        productoVentaService.eliminarProductoDeVenta(ventaId, productoId);
    }

    public void eliminarProductoConStock(Long ventaId, Long productoId) {
        productoVentaService.eliminarProductoDeVentaConStock(ventaId, productoId);
    }

    @Transactional
    public VentasDTO crearDesdePedido(Long pedidoId) {
        return crearDesdePedido(pedidoId, false);
    }

    @Transactional
    public VentasDTO crearDesdePedidoConStock(Long pedidoId) {
        return crearDesdePedido(pedidoId, true);
    }

    private VentasDTO crearDesdePedido(Long pedidoId, boolean ajustarStock) {
        Pedidos pedido = pedidosRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + pedidoId));

        Ventas venta = new Ventas();
        venta.setTotal(pedido.getTotal() == null ? 0D : pedido.getTotal());
        venta.setTotalDesc(pedido.getTotalDesc() == null ? 0D : pedido.getTotalDesc());
        venta.setMetodoPago(null);
        venta.setFecha(null);

        Ventas ventaGuardada = ventasRepository.save(venta);

        List<Producto_Pedidos> productosDelPedido = productoPedidosRepository.findAllByPedidos_Id(pedidoId);
        for (Producto_Pedidos relacion : productosDelPedido) {
            if (ajustarStock) {
                productoVentaService.crearOActualizarConStock(
                        ventaGuardada.getId(),
                        relacion.getProducto().getId(),
                        relacion.getCantidad()
                );
            } else {
                productoVentaService.crearOActualizar(
                        ventaGuardada.getId(),
                        relacion.getProducto().getId(),
                        relacion.getCantidad()
                );
            }
        }

        pedido.setEstado("Terminado");
        pedidosRepository.save(pedido);

        return toDto(ventaGuardada);
    }

    private void validate(VentasDTO ventasDTO) {
        if (ventasDTO.getTotal() == null) {
            throw new BadRequestException("El total no puede ser nulo");
        }
        if (ventasDTO.getTotal() < 0) {
            throw new BadRequestException("El total debe ser mayor o igual a 0");
        }
        if (ventasDTO.getTotalDesc() != null && ventasDTO.getTotalDesc() < 0) {
            throw new BadRequestException("El total con descuento debe ser mayor o igual a 0");
        }
    }

    private VentasDTO toDto(Ventas ventas) {
        return new VentasDTO(
                ventas.getId(),
                ventas.getTotal(),
                ventas.getTotalDesc(),
                ventas.getMetodoPago(),
                ventas.getFecha()
        );
    }

    private Ventas toEntity(VentasDTO ventasDTO) {
        Ventas ventas = new Ventas();
        ventas.setId(ventasDTO.getId());
        ventas.setTotal(ventasDTO.getTotal());
        ventas.setTotalDesc(ventasDTO.getTotalDesc() == null ? 0D : ventasDTO.getTotalDesc());
        ventas.setMetodoPago(ventasDTO.getMetodoPago());
        ventas.setFecha(ventasDTO.getFecha());
        return ventas;
    }
}
