package com.tufg.gestor_reuniones.repository;

import com.tufg.gestor_reuniones.model.Disponibilidad;
import com.tufg.gestor_reuniones.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Integer> {
    List<Disponibilidad> findByEvento(Evento evento);
}
