package com.GenMan.GenMan.Controller;

import com.GenMan.GenMan.DTO.BackfillResultDTO;
import com.GenMan.GenMan.DTO.SucursalDTO;
import com.GenMan.GenMan.Service.SucursalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apiManGen/Sucursal")
public class SucursalController {

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    public ResponseEntity<List<SucursalDTO>> getAllSucursales() {
        return ResponseEntity.ok(sucursalService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalDTO> getSucursalById(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SucursalDTO> createSucursal(@RequestBody SucursalDTO sucursalDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalService.save(sucursalDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalDTO> updateSucursal(@PathVariable Long id, @RequestBody SucursalDTO sucursalDTO) {
        return ResponseEntity.ok(sucursalService.update(id, sucursalDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSucursal(@PathVariable Long id) {
        sucursalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/backfill")
    public ResponseEntity<BackfillResultDTO> backfillNullData(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.backfillNullData(id));
    }
}
