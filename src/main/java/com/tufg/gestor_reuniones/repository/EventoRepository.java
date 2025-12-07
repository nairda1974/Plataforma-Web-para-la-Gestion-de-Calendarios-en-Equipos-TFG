package com.tufg.gestor_reuniones.repository;

import com.tufg.gestor_reuniones.model.Evento;
import com.tufg.gestor_reuniones.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    long countByEnlacePublicoStartingWith(String enlacePublico);
    @Query("SELECT DISTINCT e FROM Evento e LEFT JOIN FETCH e.anfitriones WHERE e.creador = :creador")
    List<Evento> findByCreadorConAnfitriones(@Param("creador") Usuario creador);

    @Query("SELECT DISTINCT e FROM Evento e LEFT JOIN FETCH e.anfitriones WHERE e.enlacePublico = :enlace")
    Optional<Evento> findByEnlacePublico(@Param("enlace") String enlace);
}
