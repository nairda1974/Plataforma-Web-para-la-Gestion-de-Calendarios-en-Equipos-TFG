package com.tufg.gestor_reuniones.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegistroForm {
    private String correo;
    private String contrasenia;
    private String contrasenia2;
    private String husoHorario;
    private List<String> listadoCalendarioGoogle;

}
