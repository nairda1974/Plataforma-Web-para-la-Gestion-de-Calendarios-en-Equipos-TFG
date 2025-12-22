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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "correo")
    private String correo;

    @Column(name = "contrasenia")
    private String contrasenia;

    @Column(name = "huso_horario")
    private String husoHorario;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Calendario> calendarios;

    @Column(name = "google_refresh_token", columnDefinition = "TEXT")
    private String googleRefreshToken;

    @OneToMany(mappedBy = "creador")
    private List<Evento> evento;

}
