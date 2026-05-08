package com.GenMan.GenMan.Service.TablasIntermedias;

import com.GenMan.GenMan.DTO.Relaciones.MateriaPrimaCantidadDTO;
import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.MateriaPrimaProductosDTO;
import com.GenMan.GenMan.Entities.MateriaPrima;
import com.GenMan.GenMan.Entities.Producto;
import com.GenMan.GenMan.Entities.TablasIntermedias.MateriaPrima_Productos;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.MateriaPrimaRepository;
import com.GenMan.GenMan.Repository.ProductoRepository;
import com.GenMan.GenMan.Repository.TablasIntermedias.MateriaPrima_ProductoRepository;
import com.GenMan.GenMan.Security.SucursalContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MateriaPrimaProductoService {

    private final MateriaPrima_ProductoRepository materiaPrimaProductoRepository;
    private final MateriaPrimaRepository materiaPrimaRepository;
    private final ProductoRepository productoRepository;

    public MateriaPrimaProductoService(
            MateriaPrima_ProductoRepository materiaPrimaProductoRepository,
            MateriaPrimaRepository materiaPrimaRepository,
            ProductoRepository productoRepository
    ) {
        this.materiaPrimaProductoRepository = materiaPrimaProductoRepository;
        this.materiaPrimaRepository = materiaPrimaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public MateriaPrimaProductosDTO crearOActualizar(Long materiaPrimaId, Long productoId, Double cantidad) {
        validateMateriaPrimaId(materiaPrimaId);
        validateProductoId(productoId);
        validateCantidad(cantidad);

        MateriaPrima materiaPrima = materiaPrimaRepository.findByIdAndSucursal_Id(materiaPrimaId, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Materia prima no encontrada con id: " + materiaPrimaId));

        Producto producto = productoRepository.findByIdAndSucursal_Id(productoId, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productoId));

        MateriaPrima_Productos relacion = materiaPrimaProductoRepository
                .findByMateriaPrima_IdAndProducto_Id(materiaPrimaId, productoId)
                .orElse(null);

        if (isZero(cantidad)) {
            if (relacion != null) {
                validateSameSucursal(relacion.getMateriaPrima().getSucursal() == null ? null : relacion.getMateriaPrima().getSucursal().getId());
                validateSameSucursal(relacion.getProducto().getSucursal() == null ? null : relacion.getProducto().getSucursal().getId());
                materiaPrimaProductoRepository.delete(relacion);
            }
            return new MateriaPrimaProductosDTO(materiaPrimaId, productoId, 0D);
        }

        if (relacion == null) {
            relacion = new MateriaPrima_Productos(materiaPrima, producto, cantidad);
        }

        relacion.setCantidad(cantidad);
        return toDto(materiaPrimaProductoRepository.save(relacion));
    }

    @Transactional
    public MateriaPrimaProductosDTO crearOActualizarConStock(Long materiaPrimaId, Long productoId, Double cantidad) {
        validateMateriaPrimaId(materiaPrimaId);
        validateProductoId(productoId);
        validateCantidad(cantidad);

        MateriaPrima materiaPrima = materiaPrimaRepository.findByIdAndSucursal_Id(materiaPrimaId, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Materia prima no encontrada con id: " + materiaPrimaId));

        Producto producto = productoRepository.findByIdAndSucursal_Id(productoId, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productoId));

        MateriaPrima_Productos relacion = materiaPrimaProductoRepository
                .findByMateriaPrima_IdAndProducto_Id(materiaPrimaId, productoId)
                .orElse(null);

        if (isZero(cantidad)) {
            if (relacion != null) {
                validateSameSucursal(relacion.getMateriaPrima().getSucursal() == null ? null : relacion.getMateriaPrima().getSucursal().getId());
                validateSameSucursal(relacion.getProducto().getSucursal() == null ? null : relacion.getProducto().getSucursal().getId());
                ajustarStockMateriaPrima(relacion.getMateriaPrima(), -relacion.getCantidad());
                materiaPrimaProductoRepository.delete(relacion);
            }
            return new MateriaPrimaProductosDTO(materiaPrimaId, productoId, 0D);
        }

        double cantidadAnterior = relacion == null ? 0D : relacion.getCantidad();
        double diferencia = cantidad - cantidadAnterior;

        ajustarStockMateriaPrima(materiaPrima, diferencia);

        if (relacion == null) {
            relacion = new MateriaPrima_Productos(materiaPrima, producto, cantidad);
        } else {
            validateSameSucursal(relacion.getMateriaPrima().getSucursal() == null ? null : relacion.getMateriaPrima().getSucursal().getId());
            validateSameSucursal(relacion.getProducto().getSucursal() == null ? null : relacion.getProducto().getSucursal().getId());
            relacion.setCantidad(cantidad);
        }

        return toDto(materiaPrimaProductoRepository.save(relacion));
    }

    @Transactional
    public MateriaPrimaProductosDTO cambiarCantidad(Long materiaPrimaId, Long productoId, Double cantidad) {
        validateMateriaPrimaId(materiaPrimaId);
        validateProductoId(productoId);
        validateCantidad(cantidad);

        MateriaPrima_Productos relacion = materiaPrimaProductoRepository
                .findByMateriaPrima_IdAndProducto_Id(materiaPrimaId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe relacion entre materia prima " + materiaPrimaId + " y producto " + productoId));

        validateSameSucursal(relacion.getMateriaPrima().getSucursal() == null ? null : relacion.getMateriaPrima().getSucursal().getId());
        validateSameSucursal(relacion.getProducto().getSucursal() == null ? null : relacion.getProducto().getSucursal().getId());

        if (isZero(cantidad)) {
            materiaPrimaProductoRepository.delete(relacion);
            return new MateriaPrimaProductosDTO(materiaPrimaId, productoId, 0D);
        }

        relacion.setCantidad(cantidad);
        return toDto(materiaPrimaProductoRepository.save(relacion));
    }

    @Transactional
    public MateriaPrimaProductosDTO cambiarCantidadConStock(Long materiaPrimaId, Long productoId, Double cantidad) {
        validateMateriaPrimaId(materiaPrimaId);
        validateProductoId(productoId);
        validateCantidad(cantidad);

        MateriaPrima_Productos relacion = materiaPrimaProductoRepository
                .findByMateriaPrima_IdAndProducto_Id(materiaPrimaId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe relacion entre materia prima " + materiaPrimaId + " y producto " + productoId));

        validateSameSucursal(relacion.getMateriaPrima().getSucursal() == null ? null : relacion.getMateriaPrima().getSucursal().getId());
        validateSameSucursal(relacion.getProducto().getSucursal() == null ? null : relacion.getProducto().getSucursal().getId());

        if (isZero(cantidad)) {
            ajustarStockMateriaPrima(relacion.getMateriaPrima(), -relacion.getCantidad());
            materiaPrimaProductoRepository.delete(relacion);
            return new MateriaPrimaProductosDTO(materiaPrimaId, productoId, 0D);
        }

        double diferencia = cantidad - relacion.getCantidad();

        ajustarStockMateriaPrima(relacion.getMateriaPrima(), diferencia);

        relacion.setCantidad(cantidad);
        return toDto(materiaPrimaProductoRepository.save(relacion));
    }

    public List<ProductoCantidadDTO> obtenerProductosPorMateriaPrima(Long materiaPrimaId) {
        validateMateriaPrimaId(materiaPrimaId);
        if (!materiaPrimaRepository.existsByIdAndSucursal_Id(materiaPrimaId, currentSucursalId())) {
            throw new ResourceNotFoundException("Materia prima no encontrada con id: " + materiaPrimaId);
        }

        return materiaPrimaProductoRepository.findAllByMateriaPrima_Id(materiaPrimaId)
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

    public List<MateriaPrimaCantidadDTO> obtenerMateriasPrimasPorProducto(Long productoId) {
        validateProductoId(productoId);
        if (!productoRepository.existsByIdAndSucursal_Id(productoId, currentSucursalId())) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + productoId);
        }

        return materiaPrimaProductoRepository.findAllByProducto_Id(productoId)
                .stream()
                .map(relacion -> new MateriaPrimaCantidadDTO(
                        relacion.getMateriaPrima().getId(),
                        relacion.getMateriaPrima().getNombre(),
                        relacion.getMateriaPrima().getAsset(),
                        relacion.getMateriaPrima().getUnidad(),
                        relacion.getMateriaPrima().getCantidad(),
                        relacion.getCantidad()
                ))
                .toList();
    }

    private void validateCantidad(Double cantidad) {
        if (cantidad == null) {
            throw new BadRequestException("La cantidad no puede ser nula");
        }
        if (cantidad < 0) {
            throw new BadRequestException("La cantidad debe ser mayor o igual a 0");
        }
    }

    private boolean isZero(Double cantidad) {
        return Double.compare(cantidad, 0D) == 0;
    }

    private void ajustarStockMateriaPrima(MateriaPrima materiaPrima, double diferenciaRelacion) {
        if (diferenciaRelacion == 0) {
            return;
        }

        if (diferenciaRelacion > 0 && materiaPrima.getCantidad() < diferenciaRelacion) {
            throw new BadRequestException(
                    "Stock insuficiente de materia prima. Disponible: " + materiaPrima.getCantidad()
                            + ", requerido: " + diferenciaRelacion
            );
        }

        materiaPrima.setCantidad(materiaPrima.getCantidad() - diferenciaRelacion);
    }

    private void validateMateriaPrimaId(Long materiaPrimaId) {
        if (materiaPrimaId == null) {
            throw new BadRequestException("El id de la materia prima es obligatorio");
        }
    }

    private void validateProductoId(Long productoId) {
        if (productoId == null) {
            throw new BadRequestException("El id del producto es obligatorio");
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

    private MateriaPrimaProductosDTO toDto(MateriaPrima_Productos relacion) {
        return new MateriaPrimaProductosDTO(
                relacion.getMateriaPrima().getId(),
                relacion.getProducto().getId(),
                relacion.getCantidad()
        );
    }
}
