package com.tfg.gestor_reuniones.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.tfg.gestor_reuniones.dto.RangoEvento;
import com.tfg.gestor_reuniones.model.Calendario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleApiService {
    @Value("${clientId}")
    public String clientId;
    @Value("${clientSecret}")
    public String clientSecret;
    private static final Logger logger = LoggerFactory.getLogger(GoogleApiService.class);
    private final OAuth2AuthorizedClientService clientService;
    @Autowired
    private UsuarioService usuarioService;
    public GoogleApiService(OAuth2AuthorizedClientService clientService){
        this.clientService = clientService;
    }

    // Ejemplo base: https://developers.google.com/api-client-library/java/google-api-java-client/oauth2?hl=es-419
    // https://developers.google.com/workspace/calendar/api/quickstart/java?hl=es-419
    public List<Calendario> listadoGoogleCalendar(String refreshToken) {
        List<Calendario> listadoCalendarios = new ArrayList<>();
        GoogleCredential credencialGoogle = new GoogleCredential.Builder()
                .setClientSecrets(clientId, clientSecret)
                .setTransport(new NetHttpTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .build();
        credencialGoogle.setRefreshToken(refreshToken);
        try {
            Calendar service =
                    new Calendar.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), credencialGoogle)
                            .setApplicationName("Gestor Reuniones TFG")
                            .build();
            CalendarList listCalendarApi = service.calendarList().list().execute();
            for (CalendarListEntry entry : listCalendarApi.getItems()) {
                Calendario calendario = new Calendario();
                calendario.setNombreCalendario(entry.getSummary());
                calendario.setIdentificadorNombreCalendario(entry.getId());
                listadoCalendarios.add(calendario);
            }
            return listadoCalendarios;
        } catch (IOException e) {
            logger.error("Fallo al cargar el calendario. Error: " + e);
            return null;
        }
    }
    //
    public List<RangoEvento> obtenerCalendariosGoogle(String refreshToken, LocalDate dia, String zonaHoraria, List<String> listaCalendario) {
        GoogleCredential credencialGoogle = new GoogleCredential.Builder()
                .setClientSecrets(clientId, clientSecret)
                .setTransport(new NetHttpTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .build();
        credencialGoogle.setRefreshToken(refreshToken);
        Calendar service =
                new Calendar.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), credencialGoogle)
                        .setApplicationName("Gestor Reuniones TFG")
                        .build();
        ZoneId horario = ZoneId.of(zonaHoraria);
        LocalDate diaMinimo = dia.minusDays(1);
        LocalDate diaMaximo = dia.plusDays(1);
        long minimo = diaMinimo.atStartOfDay(horario).toInstant().toEpochMilli();
        DateTime tiempoMinimo = new DateTime(minimo);
        long maximo = diaMaximo.atTime(23, 59, 59).atZone(horario).toInstant().toEpochMilli();
        DateTime tiempoMaximo = new DateTime(maximo);
        // He puesto single event a true para no perder eventos recurrentes, en orderBy en startTime para un orden ascendente
        List<RangoEvento> listaRango = new ArrayList<>();
        for (String calendario : listaCalendario) {
            try {
                Events events = service.events().list(calendario)
                        .setTimeMin(tiempoMinimo)
                        .setTimeMax(tiempoMaximo)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
                List<Event> items = events.getItems();

                for (Event evento : items) {
                    // Si es nulo es que el dia entero esta ocupado
                    if (evento.getStart().getDateTime() == null) {
                        LocalDate diaEventoEntero = LocalDate.parse(evento.getStart().getDate().toString());
                        LocalDateTime horaInicio = diaEventoEntero.atStartOfDay();
                        LocalDateTime horaFin = diaEventoEntero.atTime(23,59,59);
                        RangoEvento rango = new RangoEvento(horaInicio, horaFin);
                        listaRango.add(rango);
                    } else {
                        long inicio = evento.getStart().getDateTime().getValue();
                        long fin = evento.getEnd().getDateTime().getValue();

                        LocalDateTime horaInicio = Instant.ofEpochMilli(inicio)
                                .atZone(horario).toLocalDateTime();
                        LocalDateTime horaFin = Instant.ofEpochMilli(fin)
                                .atZone(horario).toLocalDateTime();
                        RangoEvento rango = new RangoEvento(horaInicio, horaFin);
                        listaRango.add(rango);
                    }
                }
            } catch (Exception e){
                logger.error("No se ha podido acceder al calendario: " +calendario );
                logger.error("Mensaje de error: " +e );

            }
            }


            return listaRango;
        }

    // @TODO Eliminar para la memoria https://developers.google.com/workspace/calendar/api/v3/reference/events?hl=es-419
    public Event almacenarReunionCalendario(String refreshToken, LocalDateTime fechaReunion, String zonaHoraria, Integer duracion,List<String> participantes, String nombre, String descripcion) {
        GoogleCredential credencialGoogle = new GoogleCredential.Builder()
                .setClientSecrets(clientId, clientSecret)
                .setTransport(new NetHttpTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .build();
        credencialGoogle.setRefreshToken(refreshToken);
        Calendar service =
                new Calendar.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), credencialGoogle)
                        .setApplicationName("Gestor Reuniones TFG")
                        .build();

        Event evento = new Event();
        evento.setSummary(nombre);
        evento.setDescription(descripcion);
        // Se establece el inicio de la reunion en el calendario de google
        EventDateTime inicioReunon = new EventDateTime();
        long milisegundosFechaInicio = fechaReunion.atZone(ZoneId.of(zonaHoraria)).toInstant().toEpochMilli();
        DateTime fechaInicioReunionGoogle = new DateTime(milisegundosFechaInicio);
        inicioReunon.setDateTime(fechaInicioReunionGoogle);
        inicioReunon.setTimeZone(zonaHoraria);
        evento.setStart(inicioReunon);
        // Se establece el fin de la reunion en el calendario de google
        EventDateTime finReunon = new EventDateTime();
        LocalDateTime fechaReunionFin = fechaReunion.plus(Duration.ofMinutes(duracion));
        long milisegundosFechaFin = fechaReunionFin.atZone(ZoneId.of(zonaHoraria)).toInstant().toEpochMilli();
        DateTime fechaFinReunionGoogle = new DateTime(milisegundosFechaFin);
        finReunon.setDateTime(fechaFinReunionGoogle);
        finReunon.setTimeZone(zonaHoraria);
        evento.setEnd(finReunon);
        List<EventAttendee> listaParticipantes = new ArrayList<>();
        // Se establece todos los participantes
        for (String correo : participantes) {
            EventAttendee participante = new EventAttendee();
            participante.setEmail(correo);
            listaParticipantes.add(participante);
        }
        evento.setAttendees(listaParticipantes);
        try {
            return service.events().insert("primary", evento).setSendUpdates("all")
                    .execute();

        } catch (IOException e) {
            logger.error("No se ha podido crear el evento de reunion en el calendario de google. Error: "+e);
            return null;
        }
    }

    public List<String> obtenerListaCorreoGoogle (List<String> participantes){
        List<String> listadoCorreoGoogle = new ArrayList<>();
        for(String correo: participantes) {
            String refreshToken = usuarioService.findByCorreo(correo).getGoogleRefreshToken();
            GoogleCredential credencialGoogle = new GoogleCredential.Builder()
                    .setClientSecrets(clientId, clientSecret)
                    .setTransport(new NetHttpTransport())
                    .setJsonFactory(GsonFactory.getDefaultInstance())
                    .build();
            credencialGoogle.setRefreshToken(refreshToken);
            Calendar service =
                    new Calendar.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), credencialGoogle)
                            .setApplicationName("Gestor Reuniones TFG")
                            .build();
            try {
                String correoGoogle = service.calendars().get("primary").execute().getId();
                listadoCorreoGoogle.add(correoGoogle);
            } catch (IOException e) {
                logger.error("No se ha podido la direccion de correo electronico de google. Error: "+e);
                return null;
            }
        }
        return listadoCorreoGoogle;
    }

}
