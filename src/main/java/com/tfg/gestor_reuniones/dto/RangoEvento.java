package com.tfg.gestor_reuniones.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RangoEvento {
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;

    public RangoEvento(LocalDateTime horaInicio, LocalDateTime horaFin){
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
}
