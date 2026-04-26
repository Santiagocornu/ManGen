package com.GenMan.GenMan.Repository;

import com.GenMan.GenMan.Entities.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    java.util.Optional<Sucursal> findByNombre(String nombre);
}
