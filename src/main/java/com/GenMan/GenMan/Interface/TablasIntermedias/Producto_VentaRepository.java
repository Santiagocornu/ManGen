package com.GenMan.GenMan.Interface.TablasIntermedias;

import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Producto_VentaRepository extends JpaRepository<Producto_Venta,Long> {
}
