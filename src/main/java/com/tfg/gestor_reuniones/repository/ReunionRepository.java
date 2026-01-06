package com.tfg.gestor_reuniones.repository;

import com.tfg.gestor_reuniones.model.Reunion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReunionRepository extends JpaRepository<Reunion, Integer> {
}
