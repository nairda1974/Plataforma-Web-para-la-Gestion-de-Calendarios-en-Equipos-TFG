package com.tufg.gestor_reuniones.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.tufg.gestor_reuniones.dto.RangoEvento;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleApiService {
    public static final String GOOGLE_CALENDAR_LIST_SCOPE = "https://www.googleapis.com/auth/calendar.readonly";
    public static final String clientId = "279024062971-uspchj03rsik6dok9mbf9vrb50j1i1oi.apps.googleusercontent.com";
    public static final String clientSecret = "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";


    public GoogleApiService(){
    }

    public List<String> listadoGoogleCalendar(String accessToken){
        List<String> listadoCalendarios = new ArrayList<>();

        GoogleCredentials credenciales = GoogleCredentials.create(
                        new AccessToken(accessToken, null))
                .createScoped(List.of(GOOGLE_CALENDAR_LIST_SCOPE));
        Calendar calendarioService;
        {
            try {
                calendarioService = new Calendar.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        new HttpCredentialsAdapter(credenciales))
                        .setApplicationName("TFG")
                        .build();
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        CalendarList listCalendarApi;
        {
            try {
                listCalendarApi = calendarioService.calendarList().list().execute();
                for (CalendarListEntry entry : listCalendarApi.getItems()) {
                    listadoCalendarios.add(entry.getSummary());
                }
                return listadoCalendarios;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Ejemplo basado en https://developers.google.com/workspace/calendar/api/quickstart/java?hl=es-419
    public List<RangoEvento> obtenerCalendariosGoogle(String refreshToken, LocalDate dia, String zonaHoraria, List<String> listaCalendario) throws IOException {
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
                System.err.println("No se ha podido acceder al calendario: " +calendario );
                System.err.println("Mensaje de error: " +e );

            }
            }


            return listaRango;
        }


        private List<RangoEvento> unificarHorarioComunes(List<RangoEvento> listadoSinUnificar){
        [[1,2],[3,5], [4,7]] -> [[1,2],[3,7]]


        }

        private boolean existeInterseccion(RangoEvento evento1, RangoEvento evento2){
            return evento2.getHoraInicio().isBefore(evento2.getHoraFin());
        }

}
