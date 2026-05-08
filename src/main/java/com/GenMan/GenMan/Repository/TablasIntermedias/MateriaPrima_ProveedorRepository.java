package com.GenMan.GenMan.Repository.TablasIntermedias;

import com.GenMan.GenMan.Entities.TablasIntermedias.MateriaPrima_Proveedores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaPrima_ProveedorRepository extends JpaRepository<MateriaPrima_Proveedores, MateriaPrima_Proveedores.Id> {
    Optional<MateriaPrima_Proveedores> findByMateriaPrima_IdAndProveedor_Id(Long materiaPrimaId, Long proveedorId);

    List<MateriaPrima_Proveedores> findAllByMateriaPrima_Id(Long materiaPrimaId);

    List<MateriaPrima_Proveedores> findAllByProveedor_Id(Long proveedorId);
}
