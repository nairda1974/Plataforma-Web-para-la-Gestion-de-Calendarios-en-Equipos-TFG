package com.tufg.gestor_reuniones.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "disponibilidad")
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidad")
    private Long id;

    private String dia;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "dia_semana")
    private String diaSemana;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}

