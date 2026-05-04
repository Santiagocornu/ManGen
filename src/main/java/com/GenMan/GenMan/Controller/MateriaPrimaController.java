package com.GenMan.GenMan.Controller;

import com.GenMan.GenMan.DTO.MateriaPrimaDTO;
import com.GenMan.GenMan.DTO.Relaciones.AsignarProductoDTO;
import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.MateriaPrimaProductosDTO;
import com.GenMan.GenMan.Service.MateriaPrimaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apiManGen/Materia_prima")
public class MateriaPrimaController {

    private final MateriaPrimaService materiaPrimaService;

    public MateriaPrimaController(MateriaPrimaService materiaPrimaService){
        this.materiaPrimaService = materiaPrimaService;
    }

    @GetMapping
    public ResponseEntity<List<MateriaPrimaDTO>> getAllMateriaPrima(){
        return ResponseEntity.ok(materiaPrimaService.findAll());
    }

    @PostMapping
    public ResponseEntity<MateriaPrimaDTO> createMateriaPrima(@Valid @RequestBody MateriaPrimaDTO materiaPrima) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materiaPrimaService.save(materiaPrima));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaPrimaDTO> getMateriaPrimaById(@PathVariable Long id) {
        MateriaPrimaDTO mp = materiaPrimaService.findById(id);
        return ResponseEntity.ok(mp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MateriaPrimaDTO> updateMateriaPrima(
            @PathVariable Long id,
            @Valid @RequestBody MateriaPrimaDTO materiaPrima
    ){
        return ResponseEntity.ok(materiaPrimaService.update(id,materiaPrima));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMateriaPrimaById(@PathVariable Long id){
        materiaPrimaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoCantidadDTO>> getProductosByMateriaPrima(@PathVariable Long id) {
        return ResponseEntity.ok(materiaPrimaService.getProductos(id));
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<MateriaPrimaProductosDTO> addProductoToMateriaPrima(
            @PathVariable Long id,
            @RequestBody AsignarProductoDTO asignarProductoDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(materiaPrimaService.saveOrUpdateProducto(id, asignarProductoDTO.getProductoId(), asignarProductoDTO.getCantidad()));
    }

    @PostMapping("/{id}/productos/con-stock")
    public ResponseEntity<MateriaPrimaProductosDTO> addProductoToMateriaPrimaConStock(
            @PathVariable Long id,
            @RequestBody AsignarProductoDTO asignarProductoDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(materiaPrimaService.saveOrUpdateProductoConStock(id, asignarProductoDTO.getProductoId(), asignarProductoDTO.getCantidad()));
    }

    @PatchMapping("/{id}/productos/{productoId}/cantidad")
    public ResponseEntity<MateriaPrimaProductosDTO> cambiarCantidadProducto(
            @PathVariable Long id,
            @PathVariable Long productoId,
            @RequestBody AsignarProductoDTO asignarProductoDTO
    ) {
        return ResponseEntity.ok(materiaPrimaService.cambiarCantidadProducto(id, productoId, asignarProductoDTO.getCantidad()));
    }

    @PatchMapping("/{id}/productos/{productoId}/cantidad/con-stock")
    public ResponseEntity<MateriaPrimaProductosDTO> cambiarCantidadProductoConStock(
            @PathVariable Long id,
            @PathVariable Long productoId,
            @RequestBody AsignarProductoDTO asignarProductoDTO
    ) {
        return ResponseEntity.ok(materiaPrimaService.cambiarCantidadProductoConStock(id, productoId, asignarProductoDTO.getCantidad()));
    }
}
