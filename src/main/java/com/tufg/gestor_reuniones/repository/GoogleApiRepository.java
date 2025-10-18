package com.tufg.gestor_reuniones.repository;

import com.tufg.gestor_reuniones.model.GoogleApi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleApiRepository extends JpaRepository<GoogleApi, Long> {
}
