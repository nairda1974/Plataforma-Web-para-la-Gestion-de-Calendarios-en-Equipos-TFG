package com.tufg.gestor_reuniones.service;


import com.tufg.gestor_reuniones.model.Usuario;
import com.tufg.gestor_reuniones.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioService(){

    }

    @Override
    public UserDetails loadUserByUsername(String correo) {
        Optional<Usuario> usuario = usuarioRepository.findById(correo);
        if (usuario.isEmpty()) throw new UsernameNotFoundException(correo);
        return User.withUsername(usuario.get().getCorreo())
                .password(usuario.get().getContrasenia())
                .authorities("authenticated")
                .build();
    }

}
