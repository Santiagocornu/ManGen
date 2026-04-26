package com.GenMan.GenMan.Repository;

import com.GenMan.GenMan.Entities.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidosRepository extends JpaRepository<Pedidos,Long> {
    java.util.List<Pedidos> findAllBySucursal_Id(Long sucursalId);
    java.util.Optional<Pedidos> findByIdAndSucursal_Id(Long id, Long sucursalId);
    boolean existsByIdAndSucursal_Id(Long id, Long sucursalId);
    java.util.List<Pedidos> findAllBySucursalIsNull();
}
