package com.GenMan.GenMan.Controller;

import com.GenMan.GenMan.DTO.ProveedorDTO;
import com.GenMan.GenMan.DTO.Relaciones.AsignarMateriaPrimaProveedorDTO;
import com.GenMan.GenMan.DTO.Relaciones.MateriaPrimaProveedorCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.MateriaPrimaProveedorDTO;
import com.GenMan.GenMan.Service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/apiManGen/Proveedor")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> getAllProveedores() {
        return ResponseEntity.ok(proveedorService.findAll());
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> createProveedor(@Valid @RequestBody ProveedorDTO proveedorDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.save(proveedorDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> getProveedorById(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> updateProveedor(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorDTO proveedorDTO
    ) {
        return ResponseEntity.ok(proveedorService.update(id, proveedorDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProveedorById(@PathVariable Long id) {
        proveedorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/materias-primas")
    public ResponseEntity<List<MateriaPrimaProveedorCantidadDTO>> getMateriasPrimasByProveedor(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.getMateriasPrimas(id));
    }

    @PostMapping("/{id}/materias-primas")
    public ResponseEntity<MateriaPrimaProveedorDTO> addMateriaPrimaToProveedor(
            @PathVariable Long id,
            @RequestBody AsignarMateriaPrimaProveedorDTO asignarMateriaPrimaProveedorDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proveedorService.saveOrUpdateMateriaPrima(
                        id,
                        asignarMateriaPrimaProveedorDTO.getMateriaPrimaId(),
                        asignarMateriaPrimaProveedorDTO.getMarca(),
                        asignarMateriaPrimaProveedorDTO.getPrecio()
                ));
    }

    @PatchMapping("/{id}/materias-primas/{materiaPrimaId}/oferta")
    public ResponseEntity<MateriaPrimaProveedorDTO> cambiarOfertaMateriaPrima(
            @PathVariable Long id,
            @PathVariable Long materiaPrimaId,
            @RequestBody AsignarMateriaPrimaProveedorDTO asignarMateriaPrimaProveedorDTO
    ) {
        return ResponseEntity.ok(proveedorService.cambiarOfertaMateriaPrima(
                id,
                materiaPrimaId,
                asignarMateriaPrimaProveedorDTO.getMarca(),
                asignarMateriaPrimaProveedorDTO.getPrecio()
        ));
    }

    @DeleteMapping("/{id}/materias-primas/{materiaPrimaId}")
    public ResponseEntity<Void> deleteMateriaPrimaFromProveedor(
            @PathVariable Long id,
            @PathVariable Long materiaPrimaId
    ) {
        proveedorService.eliminarMateriaPrima(id, materiaPrimaId);
        return ResponseEntity.noContent().build();
    }
}
