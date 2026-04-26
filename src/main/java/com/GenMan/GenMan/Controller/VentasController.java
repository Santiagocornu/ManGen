package com.GenMan.GenMan.Controller;

import com.GenMan.GenMan.DTO.VentasDTO;
import com.GenMan.GenMan.DTO.Relaciones.AsignarProductoDTO;
import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.ProductoVentaDTO;
import com.GenMan.GenMan.Service.VentasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apiManGen/Ventas")
public class VentasController {

    private final VentasService ventasService;

    public VentasController(VentasService ventasService) {
        this.ventasService = ventasService;
    }

    @GetMapping
    public ResponseEntity<List<VentasDTO>> getAllVentas() {
        return ResponseEntity.ok(ventasService.findAll());
    }

    @PostMapping
    public ResponseEntity<VentasDTO> createVenta(@Valid @RequestBody VentasDTO ventasDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventasService.save(ventasDTO));
    }

    @PostMapping("/desde-pedido/{pedidoId}")
    public ResponseEntity<VentasDTO> createVentaFromPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventasService.crearDesdePedido(pedidoId));
    }

    @PostMapping("/desde-pedido/{pedidoId}/con-stock")
    public ResponseEntity<VentasDTO> createVentaFromPedidoConStock(@PathVariable Long pedidoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventasService.crearDesdePedidoConStock(pedidoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentasDTO> getVentaById(@PathVariable Long id) {
        return ResponseEntity.ok(ventasService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentasDTO> updateVenta(
            @PathVariable Long id,
            @Valid @RequestBody VentasDTO ventasDTO
    ) {
        return ResponseEntity.ok(ventasService.update(id, ventasDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVentaById(@PathVariable Long id) {
        ventasService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoCantidadDTO>> getProductosByVenta(@PathVariable Long id) {
        return ResponseEntity.ok(ventasService.getProductos(id));
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<ProductoVentaDTO> addProductoToVenta(
            @PathVariable Long id,
            @RequestBody AsignarProductoDTO asignarProductoDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventasService.saveOrUpdateProducto(id, asignarProductoDTO.getProductoId(), asignarProductoDTO.getCantidad()));
    }

    @PostMapping("/{id}/productos/con-stock")
    public ResponseEntity<ProductoVentaDTO> addProductoToVentaConStock(
            @PathVariable Long id,
            @RequestBody AsignarProductoDTO asignarProductoDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventasService.saveOrUpdateProductoConStock(id, asignarProductoDTO.getProductoId(), asignarProductoDTO.getCantidad()));
    }

    @PatchMapping("/{id}/productos/{productoId}/cantidad")
    public ResponseEntity<ProductoVentaDTO> cambiarCantidadProducto(
            @PathVariable Long id,
            @PathVariable Long productoId,
            @RequestBody AsignarProductoDTO asignarProductoDTO
    ) {
        return ResponseEntity.ok(ventasService.cambiarCantidadProducto(id, productoId, asignarProductoDTO.getCantidad()));
    }

    @PatchMapping("/{id}/productos/{productoId}/cantidad/con-stock")
    public ResponseEntity<ProductoVentaDTO> cambiarCantidadProductoConStock(
            @PathVariable Long id,
            @PathVariable Long productoId,
            @RequestBody AsignarProductoDTO asignarProductoDTO
    ) {
        return ResponseEntity.ok(ventasService.cambiarCantidadProductoConStock(id, productoId, asignarProductoDTO.getCantidad()));
    }

    @DeleteMapping("/{id}/productos/{productoId}")
    public ResponseEntity<Void> deleteProductoFromVenta(
            @PathVariable Long id,
            @PathVariable Long productoId
    ) {
        ventasService.eliminarProducto(id, productoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/productos/{productoId}/con-stock")
    public ResponseEntity<Void> deleteProductoFromVentaConStock(
            @PathVariable Long id,
            @PathVariable Long productoId
    ) {
        ventasService.eliminarProductoConStock(id, productoId);
        return ResponseEntity.noContent().build();
    }
}
