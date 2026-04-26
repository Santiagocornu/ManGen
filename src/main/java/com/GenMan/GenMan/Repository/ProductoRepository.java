package com.GenMan.GenMan.Repository;

import com.GenMan.GenMan.Entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto,Long> {
    java.util.List<Producto> findAllBySucursal_Id(Long sucursalId);
    java.util.Optional<Producto> findByIdAndSucursal_Id(Long id, Long sucursalId);
    boolean existsByIdAndSucursal_Id(Long id, Long sucursalId);
    java.util.List<Producto> findAllBySucursalIsNull();
}
