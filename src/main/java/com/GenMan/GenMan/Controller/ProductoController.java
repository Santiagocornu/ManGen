package com.GenMan.GenMan.Controller;

import com.GenMan.GenMan.DTO.ProductoDTO;
import com.GenMan.GenMan.DTO.Relaciones.AsignarMateriaPrimaDTO;
import com.GenMan.GenMan.DTO.Relaciones.MateriaPrimaCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.MateriaPrimaProductosDTO;
import com.GenMan.GenMan.Service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apiManGen/Producto")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> createProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.save(productoDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> updateProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDTO
    ) {
        return ResponseEntity.ok(productoService.update(id, productoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductoById(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/materias-primas")
    public ResponseEntity<List<MateriaPrimaCantidadDTO>> getMateriasPrimasByProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getMateriasPrimas(id));
    }

    @PostMapping("/{id}/materias-primas")
    public ResponseEntity<MateriaPrimaProductosDTO> addMateriaPrimaToProducto(
            @PathVariable Long id,
            @RequestBody AsignarMateriaPrimaDTO asignarMateriaPrimaDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.saveOrUpdateMateriaPrima(id, asignarMateriaPrimaDTO.getMateriaPrimaId(), asignarMateriaPrimaDTO.getCantidad()));
    }

    @PostMapping("/{id}/materias-primas/con-stock")
    public ResponseEntity<MateriaPrimaProductosDTO> addMateriaPrimaToProductoConStock(
            @PathVariable Long id,
            @RequestBody AsignarMateriaPrimaDTO asignarMateriaPrimaDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.saveOrUpdateMateriaPrimaConStock(id, asignarMateriaPrimaDTO.getMateriaPrimaId(), asignarMateriaPrimaDTO.getCantidad()));
    }

    @PatchMapping("/{id}/materias-primas/{materiaPrimaId}/cantidad")
    public ResponseEntity<MateriaPrimaProductosDTO> cambiarCantidadMateriaPrima(
            @PathVariable Long id,
            @PathVariable Long materiaPrimaId,
            @RequestBody AsignarMateriaPrimaDTO asignarMateriaPrimaDTO
    ) {
        return ResponseEntity.ok(productoService.cambiarCantidadMateriaPrima(id, materiaPrimaId, asignarMateriaPrimaDTO.getCantidad()));
    }

    @PatchMapping("/{id}/materias-primas/{materiaPrimaId}/cantidad/con-stock")
    public ResponseEntity<MateriaPrimaProductosDTO> cambiarCantidadMateriaPrimaConStock(
            @PathVariable Long id,
            @PathVariable Long materiaPrimaId,
            @RequestBody AsignarMateriaPrimaDTO asignarMateriaPrimaDTO
    ) {
        return ResponseEntity.ok(productoService.cambiarCantidadMateriaPrimaConStock(id, materiaPrimaId, asignarMateriaPrimaDTO.getCantidad()));
    }
}
