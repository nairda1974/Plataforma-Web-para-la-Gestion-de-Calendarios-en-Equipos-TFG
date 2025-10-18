package com.tufg.gestor_reuniones.model;

import com.tufg.gestor_reuniones.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "disponibilidad")
@Getter
@Setter
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidad")
    private Long id;

    @Column(name = "dia")
    private String dia;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "dia_semana")
    private String diaSemana;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "correo")
    private Usuario usuario;
}