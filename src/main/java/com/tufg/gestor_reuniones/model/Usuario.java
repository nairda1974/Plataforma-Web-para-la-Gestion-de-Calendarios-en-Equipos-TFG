package com.tufg.gestor_reuniones.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
public class Usuario {

    @Id
    @Column(name = "correo")
    private String correo;

    @Column(name = "contraseña")
    private String contrasenia;

    @Column(name = "huso_horario")
    private String husoHorario;

    @Column(name = "google_calendar_token")
    private String googleCalendarToken;

    @OneToOne
    @JoinColumn(name = "google_api_id", referencedColumnName = "id")
    private GoogleApi googleApiId;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Disponibilidad> disponibilidadId;
}
