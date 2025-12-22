package com.tufg.gestor_reuniones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "calendario")
@Getter
@Setter
public class Calendario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calendario")
    private Integer idCalendario;

    @Column(name = "nombre_calendario")
    private String nombreCalendario;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}
