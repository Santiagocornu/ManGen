package com.GenMan.GenMan.Controller;

import com.GenMan.GenMan.DTO.PedidosDTO;
import com.GenMan.GenMan.DTO.Relaciones.AsignarProductoDTO;
import com.GenMan.GenMan.DTO.Relaciones.ProductoCantidadDTO;
import com.GenMan.GenMan.DTO.TablasIntermedias.ProductoPedidosDTO;
import com.GenMan.GenMan.Service.PedidosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apiManGen/Pedidos")
public class PedidosController {

    private final PedidosService pedidosService;

    public PedidosController(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @GetMapping
    public ResponseEntity<List<PedidosDTO>> getAllPedidos() {
        return ResponseEntity.ok(pedidosService.findAll());
    }

    @PostMapping
    public ResponseEntity<PedidosDTO> createPedido(@Valid @RequestBody PedidosDTO pedidosDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidosService.save(pedidosDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidosDTO> getPedidoById(@PathVariable Long id) {
        return ResponseEntity.ok(pedidosService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidosDTO> updatePedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidosDTO pedidosDTO
    ) {
        return ResponseEntity.ok(pedidosService.update(id, pedidosDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedidoById(@PathVariable Long id) {
        pedidosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoCantidadDTO>> getProductosByPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidosService.getProductos(id));
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<ProductoPedidosDTO> addProductoToPedido(
            @PathVariable Long id,
            @RequestBody AsignarProductoDTO asignarProductoDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidosService.saveOrUpdateProducto(id, asignarProductoDTO.getProductoId(), asignarProductoDTO.getCantidad()));
    }

    @PatchMapping("/{id}/productos/{productoId}/cantidad")
    public ResponseEntity<ProductoPedidosDTO> cambiarCantidadProducto(
            @PathVariable Long id,
            @PathVariable Long productoId,
            @RequestBody AsignarProductoDTO asignarProductoDTO
    ) {
        return ResponseEntity.ok(pedidosService.cambiarCantidadProducto(id, productoId, asignarProductoDTO.getCantidad()));
    }
}
