package com.GenMan.GenMan.Service.TablasIntermedias;

import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.ProductoVentaDTO;
import com.GenMan.GenMan.Entities.Producto;
import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Venta;
import com.GenMan.GenMan.Entities.Ventas;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.ProductoRepository;
import com.GenMan.GenMan.Repository.TablasIntermedias.Producto_VentaRepository;
import com.GenMan.GenMan.Repository.VentasRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoVentaService {

    private final Producto_VentaRepository productoVentaRepository;
    private final VentasRepository ventasRepository;
    private final ProductoRepository productoRepository;

    public ProductoVentaService(
            Producto_VentaRepository productoVentaRepository,
            VentasRepository ventasRepository,
            ProductoRepository productoRepository
    ) {
        this.productoVentaRepository = productoVentaRepository;
        this.ventasRepository = ventasRepository;
        this.productoRepository = productoRepository;
    }

    public ProductoVentaDTO crearOActualizar(Long ventaId, Long productoId, Integer cantidad) {
        validateCantidad(cantidad);

        Ventas venta = ventasRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + ventaId));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productoId));

        Producto_Venta relacion = productoVentaRepository.findByVenta_IdAndProducto_Id(ventaId, productoId)
                .orElse(new Producto_Venta(venta, producto, cantidad));

        relacion.setCantidad(cantidad);
        return toDto(productoVentaRepository.save(relacion));
    }

    public ProductoVentaDTO cambiarCantidad(Long ventaId, Long productoId, Integer cantidad) {
        validateCantidad(cantidad);

        Producto_Venta relacion = productoVentaRepository.findByVenta_IdAndProducto_Id(ventaId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe relacion entre venta " + ventaId + " y producto " + productoId));

        relacion.setCantidad(cantidad);
        return toDto(productoVentaRepository.save(relacion));
    }

    public List<ProductoCantidadDTO> obtenerProductosPorVenta(Long ventaId) {
        if (!ventasRepository.existsById(ventaId)) {
            throw new ResourceNotFoundException("Venta no encontrada con id: " + ventaId);
        }

        return productoVentaRepository.findAllByVenta_Id(ventaId)
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

    private ProductoVentaDTO toDto(Producto_Venta relacion) {
        return new ProductoVentaDTO(
                relacion.getVenta().getId(),
                relacion.getProducto().getId(),
                relacion.getCantidad()
        );
    }
}
