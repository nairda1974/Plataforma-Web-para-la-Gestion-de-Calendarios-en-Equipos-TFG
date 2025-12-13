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
        Integer Duracion = evento.getDuracion();
        if (listaDisponibilidad == null || listaDisponibilidad.isEmpty())
            throw new NullPointerException("No existe disponibilidad asociada a la reunión");
        disponibilidad = disponibilidadDia(dia.getDayOfWeek().getValue(),listaDisponibilidad);
        if(disponibilidad == null){
            return null; // El dia seleccionado por el usuario no esta entre los dias disponibles de la reunion
        }
        TemporalAmount duracion = Duration.ofMinutes(evento.getDuracion());
        for (LocalTime disponible = disponibilidad.getHoraInicio();
             disponible.isBefore(disponibilidad.getHoraFin());
             disponible = disponible.plus(duracion)){
            boolean conflicto = false;
            for(RangoEvento rangoEvento1: rangoEvento){
                if(conflictoDisponibilidad(rangoEvento1,disponible)){
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
    private boolean conflictoDisponibilidad(RangoEvento evento, LocalTime horaDisponible){
        LocalTime inicio = evento.getHoraInicio().toLocalTime();
        LocalTime fin = evento.getHoraFin().toLocalTime();
        return inicio.isAfter(horaDisponible) && fin.isBefore(horaDisponible) ||
                inicio.equals(horaDisponible) || fin.equals(horaDisponible);
    }
}
