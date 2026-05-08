package com.GenMan.GenMan.Service;

import com.GenMan.GenMan.DTO.ProveedorDTO;
import com.GenMan.GenMan.DTO.Relaciones.MateriaPrimaProveedorCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.MateriaPrimaProveedorDTO;
import com.GenMan.GenMan.Entities.Proveedor;
import com.GenMan.GenMan.Entities.Sucursal;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import com.GenMan.GenMan.Repository.ProveedorRepository;
import com.GenMan.GenMan.Repository.SucursalRepository;
import com.GenMan.GenMan.Security.SucursalContext;
import com.GenMan.GenMan.Service.TablasIntermedias.MateriaPrimaProveedorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final SucursalRepository sucursalRepository;
    private final MateriaPrimaProveedorService materiaPrimaProveedorService;

    public ProveedorService(
            ProveedorRepository proveedorRepository,
            SucursalRepository sucursalRepository,
            MateriaPrimaProveedorService materiaPrimaProveedorService
    ) {
        this.proveedorRepository = proveedorRepository;
        this.sucursalRepository = sucursalRepository;
        this.materiaPrimaProveedorService = materiaPrimaProveedorService;
    }

    public List<ProveedorDTO> findAll() {
        return proveedorRepository.findAllBySucursal_Id(currentSucursalId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public ProveedorDTO findById(Long id) {
        return proveedorRepository.findByIdAndSucursal_Id(id, currentSucursalId())
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
    }

    @Transactional
    public ProveedorDTO save(ProveedorDTO proveedorDTO) {
        validate(proveedorDTO);
        Proveedor proveedor = toEntity(proveedorDTO);
        proveedor.setSucursal(currentSucursal());
        return toDto(proveedorRepository.save(proveedor));
    }

    @Transactional
    public ProveedorDTO update(Long id, ProveedorDTO proveedorDTO) {
        validate(proveedorDTO);

        Proveedor proveedorExistente = proveedorRepository.findByIdAndSucursal_Id(id, currentSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro proveedor con id: " + id));

        proveedorExistente.setNombre(proveedorDTO.getNombre());
        proveedorExistente.setEmail(proveedorDTO.getEmail());
        proveedorExistente.setNumeroTelefono(proveedorDTO.getNumeroTelefono());
        proveedorExistente.setDescripcion(proveedorDTO.getDescripcion());

        return toDto(proveedorRepository.save(proveedorExistente));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!proveedorRepository.existsByIdAndSucursal_Id(id, currentSucursalId())) {
            throw new ResourceNotFoundException("No se pudo eliminar. Proveedor no encontrado con id: " + id);
        }
        proveedorRepository.deleteById(id);
    }

    public List<MateriaPrimaProveedorCantidadDTO> getMateriasPrimas(Long proveedorId) {
        return materiaPrimaProveedorService.obtenerMateriasPrimasPorProveedor(proveedorId);
    }

    @Transactional
    public MateriaPrimaProveedorDTO saveOrUpdateMateriaPrima(Long proveedorId, Long materiaPrimaId, String marca, Double precio) {
        return materiaPrimaProveedorService.crearOActualizar(materiaPrimaId, proveedorId, marca, precio);
    }

    @Transactional
    public MateriaPrimaProveedorDTO cambiarOfertaMateriaPrima(Long proveedorId, Long materiaPrimaId, String marca, Double precio) {
        return materiaPrimaProveedorService.cambiarOferta(materiaPrimaId, proveedorId, marca, precio);
    }

    @Transactional
    public void eliminarMateriaPrima(Long proveedorId, Long materiaPrimaId) {
        materiaPrimaProveedorService.eliminarRelacion(materiaPrimaId, proveedorId);
    }

    private void validate(ProveedorDTO proveedorDTO) {
        if (proveedorDTO.getNombre() == null || proveedorDTO.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del proveedor es obligatorio");
        }
        if (proveedorDTO.getEmail() == null || proveedorDTO.getEmail().isBlank()) {
            throw new BadRequestException("El email del proveedor es obligatorio");
        }
        if (proveedorDTO.getNumeroTelefono() == null || proveedorDTO.getNumeroTelefono().isBlank()) {
            throw new BadRequestException("El numero de telefono del proveedor es obligatorio");
        }
    }

    private ProveedorDTO toDto(Proveedor proveedor) {
        return new ProveedorDTO(
                proveedor.getId(),
                proveedor.getNombre(),
                proveedor.getEmail(),
                proveedor.getNumeroTelefono(),
                proveedor.getDescripcion()
        );
    }

    private Proveedor toEntity(ProveedorDTO proveedorDTO) {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(proveedorDTO.getId());
        proveedor.setNombre(proveedorDTO.getNombre());
        proveedor.setEmail(proveedorDTO.getEmail());
        proveedor.setNumeroTelefono(proveedorDTO.getNumeroTelefono());
        proveedor.setDescripcion(proveedorDTO.getDescripcion());
        return proveedor;
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
