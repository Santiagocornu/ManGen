package com.GenMan.GenMan.Repository;

import com.GenMan.GenMan.Entities.MateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima,Long> {
    java.util.List<MateriaPrima> findAllBySucursal_Id(Long sucursalId);
    java.util.Optional<MateriaPrima> findByIdAndSucursal_Id(Long id, Long sucursalId);
    boolean existsByIdAndSucursal_Id(Long id, Long sucursalId);
    java.util.List<MateriaPrima> findAllBySucursalIsNull();
}
