package com.GenMan.GenMan.Repository.TablasIntermedias;


import com.GenMan.GenMan.Entities.TablasIntermedias.Producto_Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Producto_PedidosRepository extends JpaRepository<Producto_Pedidos, Producto_Pedidos.Id> {
    Optional<Producto_Pedidos> findByPedidos_IdAndProducto_Id(Long pedidoId, Long productoId);

    List<Producto_Pedidos> findAllByPedidos_Id(Long pedidoId);

    List<Producto_Pedidos> findAllByProducto_Id(Long productoId);
}
