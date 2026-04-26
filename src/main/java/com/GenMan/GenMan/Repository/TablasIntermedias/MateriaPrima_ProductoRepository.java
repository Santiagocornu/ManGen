package com.GenMan.GenMan.Repository.TablasIntermedias;

import com.GenMan.GenMan.Entities.TablasIntermedias.MateriaPrima_Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaPrima_ProductoRepository extends JpaRepository<MateriaPrima_Productos, MateriaPrima_Productos.Id> {
    Optional<MateriaPrima_Productos> findByMateriaPrima_IdAndProducto_Id(Long materiaPrimaId, Long productoId);

    List<MateriaPrima_Productos> findAllByMateriaPrima_Id(Long materiaPrimaId);

    List<MateriaPrima_Productos> findAllByProducto_Id(Long productoId);
}
