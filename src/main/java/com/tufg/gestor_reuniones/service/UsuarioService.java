package com.tufg.gestor_reuniones.service;

import com.tufg.gestor_reuniones.model.Disponibilidad;
import com.tufg.gestor_reuniones.model.Usuario;
import com.tufg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tufg.gestor_reuniones.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, DisponibilidadRepository disponibilidadRepository) {
        this.usuarioRepository = usuarioRepository;
        this.disponibilidadRepository = disponibilidadRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con correo: " + correo));

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasenia())
                .build();
    }
    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).orElse(null);
    }

    public void registrarUsuario(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public List<String> obtenerCorreos(String correoConsulta){
        return usuarioRepository.correos(correoConsulta);
    }


    /*private List<Disponibilidad> setUsuarioDisponibilidad(List<Disponibilidad> disponibilidadLista, Usuario usuario){
        List<Disponibilidad> disponibilidadConUsuario = new ArrayList<>();

        for(Disponibilidad disponibilidad: disponibilidadLista){
            disponibilidad.setUsuario(usuario);
            disponibilidadConUsuario.add(disponibilidad);
        }
        return disponibilidadConUsuario;
    }*/


}
