package com.tufg.gestor_reuniones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "google_api")
@Getter
@Setter
public class GoogleApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // datetime(6) -> LocalDateTime
    private LocalDateTime fecha;

    @Column(name = "google_calendar_api")
    private String googleCalendarApi;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_acceso")
    private String tokenAcceso;

}
