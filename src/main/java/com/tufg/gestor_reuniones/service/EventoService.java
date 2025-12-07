package com.tufg.gestor_reuniones.service;

import com.tufg.gestor_reuniones.model.Disponibilidad;
import com.tufg.gestor_reuniones.model.Evento;
import com.tufg.gestor_reuniones.model.Usuario;
import com.tufg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tufg.gestor_reuniones.repository.EventoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EventoService {
    private EventoRepository eventoRepository;
    private UsuarioService usuarioService;
    private DisponibilidadRepository disponibilidadRepository;
    private final String enlacePublico = "reunion/";

    public EventoService(EventoRepository eventoRepository, UsuarioService usuarioService, DisponibilidadRepository disponibilidadRepository){
        this.eventoRepository = eventoRepository;
        this.usuarioService = usuarioService;
        this.disponibilidadRepository = disponibilidadRepository;
    }

    public Evento almacenarEvento(Evento evento){
        return eventoRepository.save(evento);
    }
    @Transactional
    public void crearEvento(String nombre, String descripcion, Integer duracion,
                            Set<String> correosParticipantes,
                            List<Disponibilidad> listaDisponibilidad){
        Evento evento = new Evento();
        evento.setNombre(nombre);
        evento.setDescripcion(descripcion);
        evento.setDuracion(duracion);
        evento.setCreador(getCreador());
        evento.setAnfitriones(setAnfitriones(correosParticipantes));
        evento.setEnlacePublico(generarEnlacePublico(nombre));
        Evento eventoTemporal = almacenarEvento(evento);
        almacenarDisponibilidad(listaDisponibilidad, eventoTemporal);
    }


    private Usuario getCreador(){
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.usuarioService.findByCorreo(correo);
    }

    private Set<Usuario> setAnfitriones(Set<String> setCorreo){
        Set<Usuario> setUsuario = new HashSet<>();
        for(String correo: setCorreo.stream().toList()){
            setUsuario.add(usuarioService.findByCorreo(correo));
        }
        return setUsuario;
    }
    private void almacenarDisponibilidad(List<Disponibilidad> listaDisponibilidad, Evento evento){
        for(Disponibilidad disponibilidad: listaDisponibilidad){
            disponibilidad.setEvento(evento);
            disponibilidadRepository.save(disponibilidad);
        }
    }
    // Necesito generar un enlace publico para ello sustituyo espacios por guion normalizo el texto en utf8 para evitar tildes
    // en caso de coincidencia pongo guion y el numero de veces que se ha encontrado mas 1 para evitar conflictos
    private String generarEnlacePublico(String nombre){
        String direccionNombre = nombre.toLowerCase().replace(" ","-");
        String enlace = enlacePublico + direccionNombre;
        ByteBuffer enlaceBuffer = StandardCharsets.UTF_8.encode(enlace);
        String enlaceUTF8 = StandardCharsets.UTF_8.decode(enlaceBuffer).toString();
        Long numeroCoincidencias = eventoRepository.countByEnlacePublicoStartingWith(enlaceUTF8);
        if(numeroCoincidencias > 0){
            enlaceUTF8 = enlaceUTF8.concat("-"+ (numeroCoincidencias+1));
        }
        return enlaceUTF8;
    }

    public List<Evento> obtenerTablaEvento(){
        return eventoRepository.findByCreadorConAnfitriones(getCreador());
    }

    public Optional<Evento> obtenerEvento(String enlacePublico){
        return eventoRepository.findByEnlacePublico(enlacePublico);
    }

}
