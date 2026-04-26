package com.GenMan.GenMan.Service;

import com.GenMan.GenMan.DTO.MateriaPrimaDTO;
import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.MateriaPrimaProductosDTO;
import com.GenMan.GenMan.Entities.MateriaPrima;
import com.GenMan.GenMan.Entities.Sucursal;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.MateriaPrimaRepository;
import com.GenMan.GenMan.Repository.SucursalRepository;
import com.GenMan.GenMan.Security.SucursalContext;
import com.GenMan.GenMan.Service.TablasIntermedias.MateriaPrimaProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaPrimaService {

    private final MateriaPrimaRepository materiaPrimaRepository;
    private final SucursalRepository sucursalRepository;
    private final MateriaPrimaProductoService materiaPrimaProductoService;

    @Autowired
    public MateriaPrimaService(
            MateriaPrimaRepository materiaPrimaRepository,
            SucursalRepository sucursalRepository,
            MateriaPrimaProductoService materiaPrimaProductoService
    ) {
        this.materiaPrimaRepository = materiaPrimaRepository;
        this.sucursalRepository = sucursalRepository;
        this.materiaPrimaProductoService = materiaPrimaProductoService;
    }

    public List<MateriaPrimaDTO> findAll() {
        return materiaPrimaRepository.findAllBySucursal_Id(currentSucursalId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public MateriaPrimaDTO findById(Long id) {
        return materiaPrimaRepository.findByIdAndSucursal_Id(id, currentSucursalId())
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Materia prima no encontrada con codigo: " + id));
    }

    public MateriaPrimaDTO save(MateriaPrimaDTO materiaPrimaDTO) {
        validate(materiaPrimaDTO);
        MateriaPrima materiaPrima = toEntity(materiaPrimaDTO);
        materiaPrima.setSucursal(currentSucursal());
        return toDto(materiaPrimaRepository.save(materiaPrima));
    }

    public MateriaPrimaDTO update(Long id, MateriaPrimaDTO materiaPrimaDTO) {
        validate(materiaPrimaDTO);

        MateriaPrima materiaPrimaExistente = materiaPrimaRepository.findByIdAndSucursal_Id(id, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro materia prima con id: " + id));

        materiaPrimaExistente.setAsset(materiaPrimaDTO.getAsset());
        materiaPrimaExistente.setNombre(materiaPrimaDTO.getNombre());
        materiaPrimaExistente.setPrecio(materiaPrimaDTO.getPrecio());
        materiaPrimaExistente.setCantidad(materiaPrimaDTO.getCantidad());
        materiaPrimaExistente.setUnidad(materiaPrimaDTO.getUnidad());

        return toDto(materiaPrimaRepository.save(materiaPrimaExistente));
    }

    public void deleteById(Long id) {
        if (!materiaPrimaRepository.existsByIdAndSucursal_Id(id, currentSucursalId())) {
            throw new ResourceNotFoundException("No se pudo eliminar. Materia prima no encontrada con codigo: " + id);
        }
        materiaPrimaRepository.deleteById(id);
    }

    public List<ProductoCantidadDTO> getProductos(Long materiaPrimaId) {
        return materiaPrimaProductoService.obtenerProductosPorMateriaPrima(materiaPrimaId);
    }

    public MateriaPrimaProductosDTO saveOrUpdateProducto(Long materiaPrimaId, Long productoId, Integer cantidad) {
        return materiaPrimaProductoService.crearOActualizar(materiaPrimaId, productoId, cantidad);
    }

    public MateriaPrimaProductosDTO cambiarCantidadProducto(Long materiaPrimaId, Long productoId, Integer cantidad) {
        return materiaPrimaProductoService.cambiarCantidad(materiaPrimaId, productoId, cantidad);
    }

    private void validate(MateriaPrimaDTO materiaPrimaDTO) {
        if (materiaPrimaDTO.getPrecio() == null) {
            throw new BadRequestException("Debe tener precio");
        }
        if (materiaPrimaDTO.getPrecio() < 0) {
            throw new BadRequestException("El precio debe ser mayor o igual a 0");
        }
        if (materiaPrimaDTO.getCantidad() == null) {
            throw new BadRequestException("La cantidad no puede ser nula");
        }
        if (materiaPrimaDTO.getCantidad() < 0) {
            throw new BadRequestException("La cantidad debe ser mayor o igual a 0");
        }
        if (materiaPrimaDTO.getUnidad() == null || materiaPrimaDTO.getUnidad().trim().isEmpty()) {
            throw new BadRequestException("La unidad no puede ser nula");
        }
    }

    private MateriaPrimaDTO toDto(MateriaPrima materiaPrima) {
        return new MateriaPrimaDTO(
                materiaPrima.getId(),
                materiaPrima.getAsset(),
                materiaPrima.getNombre(),
                materiaPrima.getPrecio(),
                materiaPrima.getUnidad(),
                materiaPrima.getCantidad()
        );
    }

    private MateriaPrima toEntity(MateriaPrimaDTO materiaPrimaDTO) {
        MateriaPrima materiaPrima = new MateriaPrima();
        materiaPrima.setId(materiaPrimaDTO.getId());
        materiaPrima.setAsset(materiaPrimaDTO.getAsset());
        materiaPrima.setNombre(materiaPrimaDTO.getNombre());
        materiaPrima.setPrecio(materiaPrimaDTO.getPrecio());
        materiaPrima.setUnidad(materiaPrimaDTO.getUnidad());
        materiaPrima.setCantidad(materiaPrimaDTO.getCantidad());
        return materiaPrima;
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
