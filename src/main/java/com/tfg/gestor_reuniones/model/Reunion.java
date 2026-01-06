package com.tfg.gestor_reuniones.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reunion")
@Getter
@Setter
public class Reunion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reunion")
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    private Evento evento;

    @Column(name = "correo_invitado")
    private String correo;

    @Column(name = "fecha_reunion")
    private LocalDate fechaReunion;

    @Column(name = "hora_reunion")
    private LocalTime horaReunion;



}
