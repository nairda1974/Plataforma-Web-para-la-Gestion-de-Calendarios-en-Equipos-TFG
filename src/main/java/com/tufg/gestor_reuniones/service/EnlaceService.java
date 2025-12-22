package com.tufg.gestor_reuniones.service;

import com.tufg.gestor_reuniones.dto.RangoEvento;
import com.tufg.gestor_reuniones.model.Disponibilidad;
import com.tufg.gestor_reuniones.model.Evento;
import com.tufg.gestor_reuniones.repository.DisponibilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnlaceService {
    @Autowired
    private DisponibilidadRepository disponibilidadRepository;
    public EnlaceService(DisponibilidadRepository disponibilidadRepository){
        this.disponibilidadRepository=disponibilidadRepository;
    }

    public List<LocalTime> calcularDisponibilidad(List<RangoEvento> rangoEvento, Evento evento, LocalDate dia){
        List<Disponibilidad> listaDisponibilidad = obtenerListaDisponibilidad(evento);
        Disponibilidad disponibilidad = null;
        List<LocalTime> listaHorarioDisponible = new ArrayList<>();
        if (listaDisponibilidad == null || listaDisponibilidad.isEmpty())
            throw new NullPointerException("No existe disponibilidad asociada a la reunión");
        disponibilidad = disponibilidadDia(dia.getDayOfWeek().getValue(),listaDisponibilidad);
        if(disponibilidad == null){
            return null; // El dia seleccionado por el usuario no esta entre los dias disponibles de la reunion
        }
        TemporalAmount duracionEvento = Duration.ofMinutes(evento.getDuracion());
        for (LocalTime disponible = disponibilidad.getHoraInicio();
             disponible.isBefore(disponibilidad.getHoraFin()) &&
             disponibilidad.getHoraFin().isBefore(disponible.plus(Duration.ofMinutes(evento.getDuracion())));
             disponible = disponible.plus(Duration.ofMinutes(15))){
            boolean conflicto = false;
            for(RangoEvento rangoEvento1: rangoEvento){
                if(conflictoDisponibilidad(rangoEvento1,disponible,disponible.plus(duracionEvento))){
                    conflicto = true;
                }
            }
            if(!conflicto)
                listaHorarioDisponible.add(disponible);
        }

        return listaHorarioDisponible;

    }
    private List<Disponibilidad> obtenerListaDisponibilidad(Evento evento){
        return disponibilidadRepository.findByEvento(evento);
    }
    private Disponibilidad disponibilidadDia(Integer dia,List<Disponibilidad> listaDisponibilidad){
        Disponibilidad disponibilidadResultado = null;
        for(Disponibilidad disponibilidad :listaDisponibilidad){
            if(disponibilidad.getDiaSemana() == dia)
                disponibilidadResultado = disponibilidad;
        }
        return disponibilidadResultado;
    }

    // Calcula todos los posibles en el que puede tener conflictos al crear una reunion según los eventos en el calendario de google
    private boolean conflictoDisponibilidad(RangoEvento evento, LocalTime horaInicioReunion,LocalTime horaFinReunion){
        LocalTime eventoInicio = evento.getHoraInicio().toLocalTime();
        LocalTime eventoFin = evento.getHoraFin().toLocalTime();

        boolean inicioReunionEntreEvento = (horaInicioReunion.isAfter(eventoInicio) || horaInicioReunion.equals(eventoInicio))
                && (horaInicioReunion.isBefore(eventoFin) || horaInicioReunion.equals(eventoFin));

        boolean finReunionEntreEvento = (horaFinReunion.isAfter(eventoInicio) || horaFinReunion.equals(eventoInicio))
                && (horaFinReunion.isBefore(eventoFin) || horaFinReunion.equals(eventoFin));

        boolean eventoEntreReunion = (horaInicioReunion.isBefore(eventoInicio) && eventoInicio.isBefore(horaFinReunion)) ||
                (horaInicioReunion.isBefore(eventoFin) && eventoFin.isBefore(horaFinReunion));

        return inicioReunionEntreEvento || finReunionEntreEvento || eventoEntreReunion;
    }


}
