package com.GenMan.GenMan.Repository.TablasIntermedias;

import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Producto_VentaRepository extends JpaRepository<Producto_Venta, Producto_Venta.Id> {
    Optional<Producto_Venta> findByVenta_IdAndProducto_Id(Long ventaId, Long productoId);

    List<Producto_Venta> findAllByVenta_Id(Long ventaId);

    List<Producto_Venta> findAllByProducto_Id(Long productoId);
}
