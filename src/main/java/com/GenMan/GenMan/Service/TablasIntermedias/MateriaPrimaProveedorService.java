package com.GenMan.GenMan.Service.TablasIntermedias;

import com.GenMan.GenMan.DTO.Relaciones.MateriaPrimaProveedorCantidadDTO;
import com.GenMan.GenMan.DTO.Relaciones.ProveedorMateriaPrimaDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.MateriaPrimaProveedorDTO;
import com.GenMan.GenMan.Entities.MateriaPrima;
import com.GenMan.GenMan.Entities.Proveedor;
import com.GenMan.GenMan.Entities.TablasIntermedias.MateriaPrima_Proveedores;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.MateriaPrimaRepository;
import com.GenMan.GenMan.Repository.ProveedorRepository;
import com.GenMan.GenMan.Repository.TablasIntermedias.MateriaPrima_ProveedorRepository;
import com.GenMan.GenMan.Security.SucursalContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MateriaPrimaProveedorService {

    private final MateriaPrima_ProveedorRepository materiaPrimaProveedorRepository;
    private final MateriaPrimaRepository materiaPrimaRepository;
    private final ProveedorRepository proveedorRepository;

    public MateriaPrimaProveedorService(
            MateriaPrima_ProveedorRepository materiaPrimaProveedorRepository,
            MateriaPrimaRepository materiaPrimaRepository,
            ProveedorRepository proveedorRepository
    ) {
        this.materiaPrimaProveedorRepository = materiaPrimaProveedorRepository;
        this.materiaPrimaRepository = materiaPrimaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Transactional
    public MateriaPrimaProveedorDTO crearOActualizar(Long materiaPrimaId, Long proveedorId, String marca, Double precio) {
        validateMateriaPrimaId(materiaPrimaId);
        validateProveedorId(proveedorId);
        validateMarca(marca);
        validatePrecio(precio);

        MateriaPrima materiaPrima = materiaPrimaRepository.findByIdAndSucursal_Id(materiaPrimaId, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Materia prima no encontrada con id: " + materiaPrimaId));

        Proveedor proveedor = proveedorRepository.findByIdAndSucursal_Id(proveedorId, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + proveedorId));

        MateriaPrima_Proveedores relacion = materiaPrimaProveedorRepository
                .findByMateriaPrima_IdAndProveedor_Id(materiaPrimaId, proveedorId)
                .orElse(null);

        if (relacion == null) {
            relacion = new MateriaPrima_Proveedores(materiaPrima, proveedor, marca, precio);
        } else {
            validateSameSucursal(relacion.getMateriaPrima().getSucursal() == null ? null : relacion.getMateriaPrima().getSucursal().getId());
            validateSameSucursal(relacion.getProveedor().getSucursal() == null ? null : relacion.getProveedor().getSucursal().getId());
            relacion.setMarca(marca);
            relacion.setPrecio(precio);
        }

        return toDto(materiaPrimaProveedorRepository.save(relacion));
    }

    @Transactional
    public MateriaPrimaProveedorDTO cambiarOferta(Long materiaPrimaId, Long proveedorId, String marca, Double precio) {
        validateMateriaPrimaId(materiaPrimaId);
        validateProveedorId(proveedorId);
        validateMarca(marca);
        validatePrecio(precio);

        MateriaPrima_Proveedores relacion = materiaPrimaProveedorRepository
                .findByMateriaPrima_IdAndProveedor_Id(materiaPrimaId, proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe relacion entre materia prima " + materiaPrimaId + " y proveedor " + proveedorId));

        validateSameSucursal(relacion.getMateriaPrima().getSucursal() == null ? null : relacion.getMateriaPrima().getSucursal().getId());
        validateSameSucursal(relacion.getProveedor().getSucursal() == null ? null : relacion.getProveedor().getSucursal().getId());

        relacion.setMarca(marca);
        relacion.setPrecio(precio);

        return toDto(materiaPrimaProveedorRepository.save(relacion));
    }

    @Transactional
    public void eliminarRelacion(Long materiaPrimaId, Long proveedorId) {
        validateMateriaPrimaId(materiaPrimaId);
        validateProveedorId(proveedorId);

        MateriaPrima_Proveedores relacion = materiaPrimaProveedorRepository
                .findByMateriaPrima_IdAndProveedor_Id(materiaPrimaId, proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe relacion entre materia prima " + materiaPrimaId + " y proveedor " + proveedorId));

        validateSameSucursal(relacion.getMateriaPrima().getSucursal() == null ? null : relacion.getMateriaPrima().getSucursal().getId());
        validateSameSucursal(relacion.getProveedor().getSucursal() == null ? null : relacion.getProveedor().getSucursal().getId());

        materiaPrimaProveedorRepository.delete(relacion);
    }

    public List<ProveedorMateriaPrimaDTO> obtenerProveedoresPorMateriaPrima(Long materiaPrimaId) {
        validateMateriaPrimaId(materiaPrimaId);
        if (!materiaPrimaRepository.existsByIdAndSucursal_Id(materiaPrimaId, currentSucursalId())) {
            throw new ResourceNotFoundException("Materia prima no encontrada con id: " + materiaPrimaId);
        }

        return materiaPrimaProveedorRepository.findAllByMateriaPrima_Id(materiaPrimaId)
                .stream()
                .map(relacion -> new ProveedorMateriaPrimaDTO(
                        relacion.getProveedor().getId(),
                        relacion.getProveedor().getNombre(),
                        relacion.getProveedor().getEmail(),
                        relacion.getProveedor().getNumeroTelefono(),
                        relacion.getProveedor().getDescripcion(),
                        relacion.getMarca(),
                        relacion.getPrecio()
                ))
                .toList();
    }

    public List<MateriaPrimaProveedorCantidadDTO> obtenerMateriasPrimasPorProveedor(Long proveedorId) {
        validateProveedorId(proveedorId);
        if (!proveedorRepository.existsByIdAndSucursal_Id(proveedorId, currentSucursalId())) {
            throw new ResourceNotFoundException("Proveedor no encontrado con id: " + proveedorId);
        }

        return materiaPrimaProveedorRepository.findAllByProveedor_Id(proveedorId)
                .stream()
                .map(relacion -> new MateriaPrimaProveedorCantidadDTO(
                        relacion.getMateriaPrima().getId(),
                        relacion.getMateriaPrima().getNombre(),
                        relacion.getMateriaPrima().getAsset(),
                        relacion.getMateriaPrima().getUnidad(),
                        relacion.getMateriaPrima().getCantidad(),
                        relacion.getMarca(),
                        relacion.getPrecio()
                ))
                .toList();
    }

    private void validateMarca(String marca) {
        if (marca == null || marca.isBlank()) {
            throw new BadRequestException("La marca es obligatoria");
        }
    }

    private void validatePrecio(Double precio) {
        if (precio == null) {
            throw new BadRequestException("El precio no puede ser nulo");
        }
        if (precio < 0) {
            throw new BadRequestException("El precio debe ser mayor o igual a 0");
        }
    }

    private void validateMateriaPrimaId(Long materiaPrimaId) {
        if (materiaPrimaId == null) {
            throw new BadRequestException("El id de la materia prima es obligatorio");
        }
    }

    private void validateProveedorId(Long proveedorId) {
        if (proveedorId == null) {
            throw new BadRequestException("El id del proveedor es obligatorio");
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

    private MateriaPrimaProveedorDTO toDto(MateriaPrima_Proveedores relacion) {
        return new MateriaPrimaProveedorDTO(
                relacion.getMateriaPrima().getId(),
                relacion.getProveedor().getId(),
                relacion.getMarca(),
                relacion.getPrecio()
        );
    }
}
