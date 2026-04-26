package com.GenMan.GenMan.Repository;

import com.GenMan.GenMan.Entities.Ventas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentasRepository extends JpaRepository<Ventas,Long> {
    java.util.List<Ventas> findAllBySucursal_Id(Long sucursalId);
    java.util.Optional<Ventas> findByIdAndSucursal_Id(Long id, Long sucursalId);
    boolean existsByIdAndSucursal_Id(Long id, Long sucursalId);
    java.util.List<Ventas> findAllBySucursalIsNull();
}
