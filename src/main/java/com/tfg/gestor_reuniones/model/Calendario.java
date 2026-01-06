package com.tfg.gestor_reuniones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "identificador_nombre_calendario")
    private String identificadorNombreCalendario;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

}
