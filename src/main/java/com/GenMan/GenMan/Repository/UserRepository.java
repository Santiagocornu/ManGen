package com.GenMan.GenMan.Repository;

import com.GenMan.GenMan.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    java.util.Optional<User> findByEmail(String email);
    java.util.Optional<User> findByEmailAndSucursal_Id(String email, Long sucursalId);
    java.util.List<User> findAllBySucursal_Id(Long sucursalId);
    java.util.Optional<User> findByIdAndSucursal_Id(Long id, Long sucursalId);
    java.util.List<User> findAllBySucursalIsNull();
}
