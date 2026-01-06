package com.tfg.gestor_reuniones.service;

import com.tfg.gestor_reuniones.model.Usuario;
import com.tfg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tfg.gestor_reuniones.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, DisponibilidadRepository disponibilidadRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.passwordEncoder = passwordEncoder;
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
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(correo));

        return User.withUsername(usuario.getCorreo()).password(usuario.getContrasenia()).roles("USER").build();
    }
    public boolean autenticarUsuario(String correo, String contraseniaPlana) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByCorreo(correo);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            return passwordEncoder.matches(contraseniaPlana, usuario.getContrasenia());
        }
        return false;
    }

}
