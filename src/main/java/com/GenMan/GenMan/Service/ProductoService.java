package com.GenMan.GenMan.Service;

import com.GenMan.GenMan.DTO.ProductoDTO;
import com.GenMan.GenMan.DTO.Relaciones.MateriaPrimaCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.MateriaPrimaProductosDTO;
import com.GenMan.GenMan.Entities.Producto;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.ProductoRepository;
import com.GenMan.GenMan.Service.TablasIntermedias.MateriaPrimaProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MateriaPrimaProductoService materiaPrimaProductoService;

    @Autowired
    public ProductoService(
            ProductoRepository productoRepository,
            MateriaPrimaProductoService materiaPrimaProductoService
    ) {
        this.productoRepository = productoRepository;
        this.materiaPrimaProductoService = materiaPrimaProductoService;
    }

    public List<ProductoDTO> findAll() {
        return productoRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public ProductoDTO findById(Long id) {
        return productoRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con codigo: " + id));
    }

    public ProductoDTO save(ProductoDTO productoDTO) {
        validate(productoDTO);
        Producto producto = toEntity(productoDTO);
        return toDto(productoRepository.save(producto));
    }

    public ProductoDTO update(Long id, ProductoDTO productoDTO) {
        validate(productoDTO);

        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro producto con id: " + id));

        productoExistente.setAsset(productoDTO.getAsset());
        productoExistente.setNombre(productoDTO.getNombre());
        productoExistente.setPrecio(productoDTO.getPrecio());
        productoExistente.setCantidad(productoDTO.getCantidad());
        productoExistente.setUnidad(productoDTO.getUnidad());

        return toDto(productoRepository.save(productoExistente));
    }

    public void deleteById(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se pudo eliminar. Producto no encontrado con codigo: " + id);
        }
        productoRepository.deleteById(id);
    }

    public List<MateriaPrimaCantidadDTO> getMateriasPrimas(Long productoId) {
        return materiaPrimaProductoService.obtenerMateriasPrimasPorProducto(productoId);
    }

    public MateriaPrimaProductosDTO saveOrUpdateMateriaPrima(Long productoId, Long materiaPrimaId, Integer cantidad) {
        return materiaPrimaProductoService.crearOActualizar(materiaPrimaId, productoId, cantidad);
    }

    public MateriaPrimaProductosDTO cambiarCantidadMateriaPrima(Long productoId, Long materiaPrimaId, Integer cantidad) {
        return materiaPrimaProductoService.cambiarCantidad(materiaPrimaId, productoId, cantidad);
    }

    private void validate(ProductoDTO productoDTO) {
        if (productoDTO.getPrecio() == null) {
            throw new BadRequestException("Debe tener precio");
        }
        if (productoDTO.getPrecio() < 0) {
            throw new BadRequestException("El precio debe ser mayor o igual a 0");
        }
        if (productoDTO.getCantidad() == null) {
            throw new BadRequestException("La cantidad no puede ser nula");
        }
        if (productoDTO.getCantidad() < 0) {
            throw new BadRequestException("La cantidad debe ser mayor o igual a 0");
        }
    }

    private ProductoDTO toDto(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getAsset(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getCantidad(),
                producto.getUnidad()
        );
    }

    private Producto toEntity(ProductoDTO productoDTO) {
        Producto producto = new Producto();
        producto.setId(productoDTO.getId());
        producto.setAsset(productoDTO.getAsset());
        producto.setNombre(productoDTO.getNombre());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setCantidad(productoDTO.getCantidad());
        producto.setUnidad(productoDTO.getUnidad());
        return producto;
    }
}
