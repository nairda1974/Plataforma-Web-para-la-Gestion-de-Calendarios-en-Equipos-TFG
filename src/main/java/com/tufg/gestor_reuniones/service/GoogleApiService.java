package com.tufg.gestor_reuniones.service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.tufg.gestor_reuniones.model.GoogleApi;
import com.tufg.gestor_reuniones.repository.GoogleApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleApiService {
    public static final String GOOGLE_CALENDAR_LIST_SCOPE = "https://www.googleapis.com/auth/calendar.readonly";
    @Autowired
    private GoogleApiRepository googleApiRepository;
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
    public GoogleApi almacenarGoogleApi(GoogleApi googleApi){
        return googleApiRepository.save(googleApi);
    }

}
