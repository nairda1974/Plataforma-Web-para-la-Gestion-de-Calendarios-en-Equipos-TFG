package com.tufg.gestor_reuniones.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {
    @Id
    private String correo;

    private String contrasenia;
    private String husoHorario;

    @Column(name = "google_calendar_token")
    private String googleCalendarToken;

    @ManyToOne
    @JoinColumn(name = "google_api_id")
    private GoogleApi googleApi;

    @OneToOne
    @JoinColumn(name = "disponibilidad_id_disponibilidad")
    private Disponibilidad disponibilidad;

}
