package com.GenMan.GenMan.Service;

import com.GenMan.GenMan.Entities.MateriaPrima;
import com.GenMan.GenMan.Exceptions.BadRequestException;
import com.GenMan.GenMan.Interface.MateriaPrimaRepository;
import com.GenMan.GenMan.Exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaPrimaService {

    private final MateriaPrimaRepository materiaPrimaRepository;

    @Autowired
    public MateriaPrimaService(MateriaPrimaRepository materiaPrimaRepository){
        this.materiaPrimaRepository = materiaPrimaRepository;
    }

    public List<MateriaPrima> findAll() {
        return materiaPrimaRepository.findAll();
    }

    public MateriaPrima findById(Long id) {
        return materiaPrimaRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Materia prima no encontrado con código: " + id));
    }

    public MateriaPrima save(MateriaPrima materiaPrima) {
        if(materiaPrima.getPrecio()==null){
            throw  new BadRequestException("Debe tener precio");
        }
        if(materiaPrima.getPrecio()<0){
            throw new BadRequestException("El precio debe ser mayor a 0");
        }
        if(materiaPrima.getCantidad()==null){
            throw new BadRequestException("La cantidad no puede ser nula");
        }
        if(materiaPrima.getUnidad()==null || materiaPrima.getUnidad().trim().isEmpty()){
            throw new BadRequestException("La unidad no puede ser nula");
        }
        return materiaPrimaRepository.save(materiaPrima);
    }

    public MateriaPrima update(Long id,MateriaPrima nuevaMateriaPrima){
        MateriaPrima materiaPrimaExistente = materiaPrimaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro materia prima con id: " + id));

        if(nuevaMateriaPrima.getPrecio()==null){
            throw new BadRequestException("Debe tener precio");
        }
        if(nuevaMateriaPrima.getPrecio()<0){
            throw new BadRequestException("El precio debe ser mayor a 0");
        }
        if(nuevaMateriaPrima.getCantidad()==null){
            throw new BadRequestException("La cantidad no puede ser nula");
        }
        if(nuevaMateriaPrima.getUnidad()==null || nuevaMateriaPrima.getUnidad().trim().isEmpty()){
            throw new BadRequestException("La unidad no puede ser nula");
        }

        materiaPrimaExistente.setAsset(nuevaMateriaPrima.getAsset());
        materiaPrimaExistente.setNombre(nuevaMateriaPrima.getNombre());
        materiaPrimaExistente.setPrecio(nuevaMateriaPrima.getPrecio());
        materiaPrimaExistente.setCantidad(nuevaMateriaPrima.getCantidad());
        materiaPrimaExistente.setUnidad(nuevaMateriaPrima.getUnidad());

        return materiaPrimaRepository.save(materiaPrimaExistente);
    }

    public void deleteById(Long id) {
        if (!materiaPrimaRepository.existsById(id)){
            throw new ResourceNotFoundException("No se mudo eliminar. Materia prima no ecnontrada con codigo: " + id);
        }
        materiaPrimaRepository.deleteById(id);
    }
}
