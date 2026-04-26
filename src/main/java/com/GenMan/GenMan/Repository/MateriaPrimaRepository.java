package com.GenMan.GenMan.Repository;

import com.GenMan.GenMan.Entities.MateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima,Long> {
}
