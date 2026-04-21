package com.GenMan.GenMan.Interface;

import com.GenMan.GenMan.Entities.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidosRepository extends JpaRepository<Pedidos,Long> {
}
