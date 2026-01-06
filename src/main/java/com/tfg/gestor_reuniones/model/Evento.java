package com.tfg.gestor_reuniones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "evento")
@Getter
@Setter
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "duracion")
    private Integer duracion;

    @Column(name = "enlace_publico")
    private String enlacePublico;

    @Column(name = "descripcion" , columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;

    @ManyToMany
    @JoinTable(
            name = "evento_anfitrion",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> anfitriones = new HashSet<>();
}