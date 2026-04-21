package com.GenMan.GenMan.Interface.TablasIntermedias;


import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Producto_PedidosRepository extends JpaRepository<Producto_Pedidos,Long> {
}
