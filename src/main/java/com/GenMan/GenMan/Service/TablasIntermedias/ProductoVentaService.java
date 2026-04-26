package com.GenMan.GenMan.Service.TablasIntermedias;

import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.ProductoVentaDTO;
import com.GenMan.GenMan.Entities.TablasIntermedias.MateriaPrima_Productos;
import com.GenMan.GenMan.Entities.Producto;
import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Venta;
import com.GenMan.GenMan.Entities.Ventas;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.ProductoRepository;
import com.GenMan.GenMan.Repository.TablasIntermedias.MateriaPrima_ProductoRepository;
import com.GenMan.GenMan.Repository.TablasIntermedias.Producto_VentaRepository;
import com.GenMan.GenMan.Repository.VentasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoVentaService {

    private final Producto_VentaRepository productoVentaRepository;
    private final VentasRepository ventasRepository;
    private final ProductoRepository productoRepository;
    private final MateriaPrima_ProductoRepository materiaPrimaProductoRepository;

    public ProductoVentaService(
            Producto_VentaRepository productoVentaRepository,
            VentasRepository ventasRepository,
            ProductoRepository productoRepository,
            MateriaPrima_ProductoRepository materiaPrimaProductoRepository
    ) {
        this.productoVentaRepository = productoVentaRepository;
        this.ventasRepository = ventasRepository;
        this.productoRepository = productoRepository;
        this.materiaPrimaProductoRepository = materiaPrimaProductoRepository;
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

    @Transactional
    public ProductoVentaDTO crearOActualizarConStock(Long ventaId, Long productoId, Integer cantidad) {
        validateCantidad(cantidad);

        Ventas venta = ventasRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + ventaId));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productoId));

        Producto_Venta relacion = productoVentaRepository.findByVenta_IdAndProducto_Id(ventaId, productoId)
                .orElse(null);

        int cantidadAnterior = relacion == null ? 0 : relacion.getCantidad();
        int diferencia = cantidad - cantidadAnterior;

        ajustarStock(producto, diferencia);

        if (relacion == null) {
            relacion = new Producto_Venta(venta, producto, cantidad);
        } else {
            relacion.setCantidad(cantidad);
        }

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

    @Transactional
    public ProductoVentaDTO cambiarCantidadConStock(Long ventaId, Long productoId, Integer cantidad) {
        validateCantidad(cantidad);

        Producto_Venta relacion = productoVentaRepository.findByVenta_IdAndProducto_Id(ventaId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe relacion entre venta " + ventaId + " y producto " + productoId));

        int diferencia = cantidad - relacion.getCantidad();

        ajustarStock(relacion.getProducto(), diferencia);

        relacion.setCantidad(cantidad);
        return toDto(productoVentaRepository.save(relacion));
    }

    public void eliminarProductoDeVenta(Long ventaId, Long productoId) {
        Producto_Venta relacion = productoVentaRepository.findByVenta_IdAndProducto_Id(ventaId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe relacion entre venta " + ventaId + " y producto " + productoId));

        productoVentaRepository.delete(relacion);
    }

    @Transactional
    public void eliminarProductoDeVentaConStock(Long ventaId, Long productoId) {
        Producto_Venta relacion = productoVentaRepository.findByVenta_IdAndProducto_Id(ventaId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe relacion entre venta " + ventaId + " y producto " + productoId));

        ajustarStock(relacion.getProducto(), -relacion.getCantidad());
        productoVentaRepository.delete(relacion);
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

    private void ajustarStock(Producto producto, int diferenciaVenta) {
        if (diferenciaVenta == 0) {
            return;
        }

        validarStockDisponible(producto, diferenciaVenta);

        producto.setCantidad(producto.getCantidad() - diferenciaVenta);

        List<MateriaPrima_Productos> materiasRelacionadas = materiaPrimaProductoRepository
                .findAllByProducto_Id(producto.getId());

        for (MateriaPrima_Productos relacionMateria : materiasRelacionadas) {
            double ajuste = relacionMateria.getCantidad() * (double) diferenciaVenta;
            relacionMateria.getMateriaPrima().setCantidad(
                    relacionMateria.getMateriaPrima().getCantidad() - ajuste
            );
        }
    }

    private void validarStockDisponible(Producto producto, int diferenciaVenta) {
        if (diferenciaVenta <= 0) {
            return;
        }

        if (producto.getCantidad() < diferenciaVenta) {
            throw new BadRequestException(
                    "Stock insuficiente del producto. Disponible: " + producto.getCantidad() + ", requerido: " + diferenciaVenta
            );
        }

        List<MateriaPrima_Productos> materiasRelacionadas = materiaPrimaProductoRepository
                .findAllByProducto_Id(producto.getId());

        for (MateriaPrima_Productos relacionMateria : materiasRelacionadas) {
            double requerido = relacionMateria.getCantidad() * (double) diferenciaVenta;
            double disponible = relacionMateria.getMateriaPrima().getCantidad();

            if (disponible < requerido) {
                throw new BadRequestException(
                        "Stock insuficiente de materia prima '" + relacionMateria.getMateriaPrima().getNombre()
                                + "'. Disponible: " + disponible + ", requerido: " + requerido
                );
            }
        }
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
