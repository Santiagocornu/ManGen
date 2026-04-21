package com.GenMan.GenMan.Controller;

import com.GenMan.GenMan.Entities.MateriaPrima;
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
    public ResponseEntity<List<MateriaPrima>> getAllMateriaPrima(){
        return ResponseEntity.ok(materiaPrimaService.findAll());
    }

    @PostMapping
    public ResponseEntity<MateriaPrima> createMateriaPrima(@Valid @RequestBody MateriaPrima materiaPrima) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materiaPrimaService.save(materiaPrima));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaPrima> getMateriaPrimaById(@PathVariable Long id) {
        MateriaPrima mp = materiaPrimaService.findById(id);
        return ResponseEntity.ok(mp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MateriaPrima> updateMateriaPrima(
            @PathVariable Long id,
            @RequestBody MateriaPrima materiaPrima
    ){
        return ResponseEntity.ok(materiaPrimaService.update(id,materiaPrima));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMateriaPrimaById(@PathVariable Long id){
        materiaPrimaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
