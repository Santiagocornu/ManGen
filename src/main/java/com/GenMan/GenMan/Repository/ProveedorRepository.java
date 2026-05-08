package com.GenMan.GenMan.Repository;

import com.GenMan.GenMan.Entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    java.util.List<Proveedor> findAllBySucursal_Id(Long sucursalId);
    java.util.Optional<Proveedor> findByIdAndSucursal_Id(Long id, Long sucursalId);
    boolean existsByIdAndSucursal_Id(Long id, Long sucursalId);
    java.util.List<Proveedor> findAllBySucursalIsNull();
}
