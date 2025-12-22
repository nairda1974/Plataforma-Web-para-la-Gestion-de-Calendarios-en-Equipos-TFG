package com.tufg.gestor_reuniones.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.tufg.gestor_reuniones.dto.RangoEvento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.GeneralSecurityException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleApiService {
    @Value("${google.calendar.scope }")
    public String GOOGLE_CALENDAR_LIST_SCOPE;
    @Value("${clientId}")
    public String clientId;
    @Value("${clientSecret}")
    public String clientSecret;
    private static final Logger logger = LoggerFactory.getLogger(GoogleApiService.class);

    public GoogleApiService(){
    }

    public List<String> listadoGoogleCalendar(String accessToken) {
        List<String> listadoCalendarios = new ArrayList<>();

        GoogleCredentials credenciales = GoogleCredentials.create(
                        new AccessToken(accessToken, null))
                .createScoped(List.of(GOOGLE_CALENDAR_LIST_SCOPE));
        Calendar calendarioService;

        try {
            calendarioService = new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credenciales))
                    .setApplicationName("TFG")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            logger.error("Fallo al cargar el calendario. Error: " + e);
            return null;
        }

        CalendarList listCalendarApi;

        try {
            listCalendarApi = calendarioService.calendarList().list().execute();
            for (CalendarListEntry entry : listCalendarApi.getItems()) {
                listadoCalendarios.add(entry.getSummary());
            }
            return listadoCalendarios;
        } catch (IOException e) {
            logger.error("No se ha podido obtener el listado de calencarios del usuario. Error: " + e);
            return new ArrayList<>();
        }

    }

    // @TODO Eliminar para la memoria https://developers.google.com/workspace/calendar/api/quickstart/java?hl=es-419
    public List<RangoEvento> obtenerCalendariosGoogle(String refreshToken, LocalDate dia, String zonaHoraria, List<String> listaCalendario) {
        NetHttpTransport transport = new NetHttpTransport();
        // permite obtener la credencial del usuario para nuestro proyecto a partir del refresh token del usuario y la identificacion del
        // proyecto el clientId y el secretId
        UserCredentials credencialesUsuario = UserCredentials.newBuilder().setRefreshToken(refreshToken).setClientId(clientId).setClientSecret(clientSecret).build();

        // Para poder usar la clase calendar necesito pasar de UserCredentials a httpRequestInitializer
        HttpCredentialsAdapter adaptadorCredencial = new HttpCredentialsAdapter(credencialesUsuario);
        Calendar service =
                new Calendar.Builder(transport, GsonFactory.getDefaultInstance(), adaptadorCredencial)
                        .setApplicationName("Gestor Reuniones TFG")
                        .build();
        ZoneId horario = ZoneId.of(zonaHoraria);
        long minimo = dia.atStartOfDay(horario).toInstant().toEpochMilli();
        DateTime tiempoMinimo = new DateTime(minimo);
        long maximo = dia.atTime(23, 59, 59).atZone(horario).toInstant().toEpochMilli();
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
                        continue;
                    }
                    long inicio = evento.getStart().getDateTime().getValue();
                    long fin = evento.getEnd().getDateTime().getValue();

                    LocalDateTime horaInicio = Instant.ofEpochMilli(inicio)
                            .atZone(horario).toLocalDateTime();
                    LocalDateTime horaFin = Instant.ofEpochMilli(fin)
                            .atZone(horario).toLocalDateTime();
                    RangoEvento rango = new RangoEvento(horaInicio, horaFin);
                    listaRango.add(rango);
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
        NetHttpTransport transport = new NetHttpTransport();
        // permite obtener la credencial del usuario para nuestro proyecto a partir del refresh token del usuario y la identificacion del
        // proyecto el clientId y el secretId
        UserCredentials credencialesUsuario = UserCredentials.newBuilder().setRefreshToken(refreshToken).setClientId(clientId).setClientSecret(clientSecret).build();

        // Para poder usar la clase calendar necesito pasar de UserCredentials a httpRequestInitializer
        HttpCredentialsAdapter adaptadorCredencial = new HttpCredentialsAdapter(credencialesUsuario);
        Calendar service =
                new Calendar.Builder(transport, GsonFactory.getDefaultInstance(), adaptadorCredencial)
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
        for (String correo : participantes) { // Suponiendo que tienes una lista de Strings
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




}
