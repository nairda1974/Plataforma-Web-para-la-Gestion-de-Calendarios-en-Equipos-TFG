package com.tufg.gestor_reuniones.repository;

import com.tufg.gestor_reuniones.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);
    @Query(value = "SELECT u.correo FROM Usuario u WHERE lower(u.correo) LIKE lower(concat('%', :texto, '%'))")
    List<String> correos(String texto);
}
