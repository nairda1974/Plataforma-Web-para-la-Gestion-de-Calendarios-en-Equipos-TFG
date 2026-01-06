package com.tfg.gestor_reuniones.service;

import com.tfg.gestor_reuniones.dto.RangoEvento;
import com.tfg.gestor_reuniones.model.Disponibilidad;
import com.tfg.gestor_reuniones.model.Evento;
import com.tfg.gestor_reuniones.model.Reunion;
import com.tfg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tfg.gestor_reuniones.repository.EventoRepository;
import com.tfg.gestor_reuniones.repository.ReunionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReunionService {
    @Autowired
    private DisponibilidadRepository disponibilidadRepository;
    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private ReunionRepository reunionRepository;

    public ReunionService(DisponibilidadRepository disponibilidadRepository, EventoRepository eventoRepository, ReunionRepository reunionRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.eventoRepository = eventoRepository;
        this.reunionRepository = reunionRepository;
    }

    public List<LocalDateTime> calcularDisponibilidad(List<RangoEvento> rangoEvento, Evento evento, LocalDate dia, String zonaHorariaInvitado) {
        List<Disponibilidad> listaDisponibilidad = obtenerListaDisponibilidad(evento);
        Disponibilidad disponibilidad = null;
        List<LocalDateTime> listaHorarioDisponible = new ArrayList<>();
        if (listaDisponibilidad == null || listaDisponibilidad.isEmpty())
            throw new NullPointerException("No existe disponibilidad asociada a la reunión");
        disponibilidad = disponibilidadDia(dia.getDayOfWeek().getValue(), listaDisponibilidad);
        if (disponibilidad == null) {
            return null; // El dia seleccionado por el usuario no esta entre los dias disponibles de la reunion
        }
        // Genero el objeto ZonedDateTime al huso horario del creador del evento y lo convierto al ZonedDeteTime del huso horario del invitado
        // De esta forma se adapta al distinto huso horario
        ZonedDateTime zonaInicioCreador = ZonedDateTime.of(dia,disponibilidad.getHoraInicio(), ZoneId.of(evento.getCreador().getHusoHorario()));
        ZonedDateTime zonaFinCreador = ZonedDateTime.of(dia,disponibilidad.getHoraFin(), ZoneId.of(evento.getCreador().getHusoHorario()));
        ZonedDateTime zonaInicioInvitado = zonaInicioCreador.withZoneSameInstant(ZoneId.of(zonaHorariaInvitado));
        ZonedDateTime zonaFinInvitado = zonaFinCreador.withZoneSameInstant(ZoneId.of(zonaHorariaInvitado));
        LocalDateTime horaInicio = zonaInicioInvitado.toLocalDateTime();
        LocalDateTime horaFin = zonaFinInvitado.toLocalDateTime();


        // Uso LocalDateTime para tener en cuenta un huso horario el creador su disponibilidad termina a la 10 noche y el invitado tiene
        // huso horario +5 y no de error.
        TemporalAmount duracionEvento = Duration.ofMinutes(evento.getDuracion());
        for (LocalDateTime disponible = horaInicio;
             disponible.plus(Duration.ofMinutes(evento.getDuracion())).isBefore(horaFin) || disponible.plus(Duration.ofMinutes(evento.getDuracion())).equals(horaFin);
             disponible = disponible.plus(Duration.ofMinutes(15))) {

            boolean conflicto = false;
            for (RangoEvento rangoEvento1 : rangoEvento) {
                if (conflictoDisponibilidad(rangoEvento1, disponible, disponible.plus(duracionEvento))) {
                    conflicto = true;
                }
            }
            if (!conflicto)
                listaHorarioDisponible.add(disponible);
        }

        return listaHorarioDisponible;

    }

    public String obtenerDiaDisponible(Evento evento){
        List<Disponibilidad> listaDisponibilidad = obtenerListaDisponibilidad(evento);
        String diaDisponible = "";
        int numDisponibilidad = 0;
        for(Disponibilidad disponibilidad: listaDisponibilidad){
            if(listaDisponibilidad.size()-1 > numDisponibilidad)
                diaDisponible = diaDisponible + diaSemana(disponibilidad.getDiaSemana()) +", ";
            if(listaDisponibilidad.size()-1 == numDisponibilidad)
                diaDisponible = diaDisponible + diaSemana(disponibilidad.getDiaSemana());
            numDisponibilidad++;
        }
        return diaDisponible;
    }

    private String diaSemana(int id) {
        switch(id) {
            case 1:
                return "Lunes";
            case 2:
                return "Martes";
            case 3:
                return "Mi\u00E9rcoles";
            case 4:
                return "Jueves";
            case 5:
                return "Viernes";
            case 6:
                return "S\u00E1bado";
            case 7:
                return "Domingo";
            default:
                return "";
        }
    }

    private List<Disponibilidad> obtenerListaDisponibilidad(Evento evento) {
        return disponibilidadRepository.findByEvento(evento);
    }

    private Disponibilidad disponibilidadDia(Integer dia, List<Disponibilidad> listaDisponibilidad) {
        Disponibilidad disponibilidadResultado = null;
        for (Disponibilidad disponibilidad : listaDisponibilidad) {
            if (disponibilidad.getDiaSemana() == dia)
                disponibilidadResultado = disponibilidad;
        }
        return disponibilidadResultado;
    }

    // Calcula todos los posibles en el que puede tener conflictos al crear una reunion según los eventos en el calendario de google
    private boolean conflictoDisponibilidad(RangoEvento evento, LocalDateTime horaInicioReunion, LocalDateTime horaFinReunion) {
        LocalDateTime eventoInicio = evento.getHoraInicio();
        LocalDateTime eventoFin = evento.getHoraFin();

        boolean inicioReunionEntreEvento = (horaInicioReunion.isAfter(eventoInicio) || horaInicioReunion.equals(eventoInicio))
                && (horaInicioReunion.isBefore(eventoFin));

        boolean finReunionEntreEvento = (horaFinReunion.isAfter(eventoInicio))
                && (horaFinReunion.isBefore(eventoFin) || horaFinReunion.equals(eventoFin));

        boolean eventoEntreReunion = (horaInicioReunion.isBefore(eventoInicio) && eventoInicio.isBefore(horaFinReunion)) ||
                (horaInicioReunion.isBefore(eventoFin) && eventoFin.isBefore(horaFinReunion));

        return inicioReunionEntreEvento || finReunionEntreEvento || eventoEntreReunion;
    }


    public void almacenarReunion(String correo, LocalDateTime horaReunion, String enlacePublico){
    Reunion reunion = new Reunion();
    Evento evento = eventoRepository.findByEnlacePublico(enlacePublico).orElse(null);
    reunion.setFechaReunion(horaReunion.toLocalDate());
    reunion.setHoraReunion(horaReunion.toLocalTime());
    reunion.setCorreo(correo);
    reunion.setEvento(evento);
    reunionRepository.save(reunion);
    }



}
