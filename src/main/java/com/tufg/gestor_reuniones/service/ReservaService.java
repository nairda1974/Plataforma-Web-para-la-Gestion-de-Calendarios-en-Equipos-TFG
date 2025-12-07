package com.tufg.gestor_reuniones.service;

import com.tufg.gestor_reuniones.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReservaService {
    private final UsuarioRepository usuarioRepository;

    public ReservaService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


}
