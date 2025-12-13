package com.tufg.gestor_reuniones.view;

import com.tufg.gestor_reuniones.dto.RangoEvento;
import com.tufg.gestor_reuniones.model.Evento;
import com.tufg.gestor_reuniones.model.Usuario;
import com.tufg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tufg.gestor_reuniones.repository.EventoRepository;
import com.tufg.gestor_reuniones.service.EnlaceService;
import com.tufg.gestor_reuniones.service.EventoService;
import com.tufg.gestor_reuniones.service.GoogleApiService;
import com.tufg.gestor_reuniones.service.UsuarioService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Route(value = "reunion")
@PageTitle("reunion")
@PermitAll
@AnonymousAllowed
public class EnlacePublicoView extends VerticalLayout implements HasUrlParameter<String> {
    public EventoService eventoService;
    public Evento evento;
    private final GoogleApiService googleApiService;
    @Autowired
    public DisponibilidadRepository disponibilidadRepository;
    public EnlacePublicoView(EventoRepository eventoRepository, UsuarioService usuarioService, DisponibilidadRepository disponibilidadRepository,
                             GoogleApiService googleApiService) {
        // Header
        setSizeFull();
        H1 cabecera = new H1("Gestor de Reuniones");
        cabecera.getElement().getStyle().set("text-align", "center");
        this.googleApiService = googleApiService;
        this.eventoService = new EventoService(eventoRepository, usuarioService , disponibilidadRepository);
        this.disponibilidadRepository = disponibilidadRepository;
        HorizontalLayout header = new HorizontalLayout(cabecera);
        header.setAlignItems(Alignment.CENTER);
        header.setFlexGrow(1, cabecera);
        header.setPadding(true);
        header.setSpacing(true);
        header.setWidthFull();
        header.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        header.addClassName("box-shadow-s");
        add(header);
    }

    @Override
    public void setParameter(BeforeEvent event,
                             String parameter) {
       Optional<Evento> eventoOptional =  eventoService.obtenerEvento("reunion/"+parameter);
       if(eventoOptional.isPresent()){
           evento = eventoOptional.get();
           VerticalLayout izquierda = izquierdaLayout();
           VerticalLayout derecha = derechaLayout();
           izquierda.setWidth("40%");
           derecha.setWidth("60%");
           HorizontalLayout tarjeta = new HorizontalLayout(izquierda, derecha);
           tarjeta.setSizeFull();
           add(tarjeta);
           setFlexGrow(1, tarjeta);
       } else{
           System.out.println("Error no se ha podido recuperar el evento");
       }
    }
    private Button nuevaReunion(){
        Button nuevaReunnion = new Button("Crear Reunión", new Icon(VaadinIcon.FILE_ADD));
        nuevaReunnion.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nuevaReunnion.getStyle().set("margin-top", "var(--lumo-space-l)");
        nuevaReunnion.setIconAfterText(true);
        nuevaReunnion.getStyle()
                .set("text-decoration", "none")
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        return nuevaReunnion;
    }



    private VerticalLayout izquierdaLayout(){
        VerticalLayout izquierdaLayout = new VerticalLayout();
        izquierdaLayout.setWidthFull();
        String nombre = "";
        if(evento.getNombre() != null && evento.getNombre().length()>1)
             nombre = evento.getNombre().substring(0,1).toUpperCase() + evento.getNombre().substring(1);
        HorizontalLayout titulo = new HorizontalLayout();
        H2 nombreCampo = new H2(nombre);
        nombreCampo.getStyle().set("color", "#1e3a8a");
        Icon calendarioIcono = new Icon(VaadinIcon.CALENDAR);
        calendarioIcono.setColor("#1e3a8a");
        titulo.add(calendarioIcono, nombreCampo);
        // representa el nombre de la reunion

        // representa la duracion de la reunion
        HorizontalLayout duracionLayout = new HorizontalLayout();
        Span duracion = new Span(evento.getDuracion() + " minutos");
        duracion.getStyle().set("white-space", "pre-wrap");
        duracion.getStyle().set("color", "#444");
        duracionLayout.add(new Icon(VaadinIcon.USER_CLOCK), duracion);

        // Da una descripcion mas detallada sobre la reunion
        H3 descripcionCampo = new H3("Detalles");

        Span descripcion= new Span(evento.getDescripcion());
        descripcion.getStyle().set("white-space", "pre-wrap");
        descripcion.getStyle().set("color", "#444");

        VerticalLayout listadoParticipante = new VerticalLayout();
        H3 participantesCampo = new H3("Participantes");

        // Listado con todos los participantes
        for(Usuario participante: evento.getAnfitriones()){
            Avatar iconoUsuario = new Avatar(participante.getCorreo());
            Span correo = new Span(participante.getCorreo());
            correo.getStyle().set("white-space", "pre-wrap");
            correo.getStyle().set("color", "#444");
            HorizontalLayout participanteLayout = new HorizontalLayout();
            participanteLayout.add(iconoUsuario,correo);
            listadoParticipante.add(participanteLayout);
        }
        Scroller scroller = new Scroller(listadoParticipante);
        scroller.addClassNames(LumoUtility.Border.BOTTOM, LumoUtility.Padding.MEDIUM);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.setMaxHeight("20hv");
        izquierdaLayout.add(titulo, duracionLayout,descripcionCampo,descripcion,participantesCampo,scroller);
        return izquierdaLayout;
    }

//    private VerticalLayout derechaLayout(){
//        VerticalLayout derechaLayout = new VerticalLayout();
//        derechaLayout.setWidthFull();
//        derechaLayout.add(new H3("Detalles"));
//        Span descripcionTexto = new Span(evento.getDescripcion());
//        descripcionTexto.getStyle().set("white-space", "pre-wrap");
//        derechaLayout.getStyle().set("background-color", "#F8F9FA");
//        derechaLayout.getStyle().set("border-left", "1px solid #ddd");
//        EmailField correo = new EmailField("Correo");
//
//        DatePicker fechaReunión = new DatePicker("Fecha Reunión");
//        TimePicker horaReunion = new TimePicker("Hora Reunión");
//
//        Button finalizar = new Button("Finalizar");
//
//        derechaLayout.add(descripcionTexto,correo,fechaReunión,horaReunion,finalizar);
//
//        return derechaLayout;
//    }

    private VerticalLayout derechaLayout() {
        VerticalLayout derechaLayout = new VerticalLayout();
        derechaLayout.setWidthFull();
        derechaLayout.getStyle().set("background-color", "#F8F9FA");
        derechaLayout.getStyle().set("border-left", "1px solid #ddd");
        derechaLayout.setPadding(true);

        // 1. Elementos Visuales Estándar
        derechaLayout.add(new H3("Detalles"));
        Span descripcionTexto = new Span(evento.getDescripcion());
        descripcionTexto.getStyle().set("white-space", "pre-wrap");

        EmailField correo = new EmailField("Tu Correo");
        correo.setWidthFull();

        DatePicker fechaReunion = new DatePicker("Fecha Reunión");
        fechaReunion.setWidthFull();
        fechaReunion.setValue(java.time.LocalDate.now()); // Por defecto hoy para facilitar el test

        TimePicker horaReunion = new TimePicker("Hora Reunión");
        horaReunion.setWidthFull();

        Button finalizar = new Button("Finalizar Reserva");
        finalizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        finalizar.setWidthFull();

        // ============================================================
        // ZONA DE PRUEBAS (SOLO PARA DESARROLLO)
        // ============================================================
        H3 tituloTest = new H3("🧪 Zona de Test Google Calendar");
        tituloTest.getStyle().set("color", "red");

        Button btnTest = new Button("Consultar Disponibilidad Anfitrión", new Icon(VaadinIcon.SEARCH));
        TextArea areaResultados = new TextArea("Resultados de la llamada:");
        areaResultados.setWidthFull();
        areaResultados.setHeight("150px");
        areaResultados.setReadOnly(true);

        btnTest.addClickListener(e -> {
            try {
                // A. Obtener datos necesarios
                if (evento.getAnfitriones().isEmpty()) {
                    Notification.show("Error: Este evento no tiene anfitriones asignados.");
                    return;
                }
                // Cogemos al primer anfitrión del Set
                Usuario anfitrion = evento.getAnfitriones().iterator().next();

                String token = anfitrion.getGoogleRefreshToken();
                String zona = anfitrion.getHusoHorario();
                LocalDate dia = fechaReunion.getValue();

                // Validaciones rápidas para el test
                if (token == null || token.isEmpty()) {
                    areaResultados.setValue("ERROR: El anfitrión (" + anfitrion.getCorreo() + ") no tiene Refresh Token vinculado.");
                    return;
                }
                if (zona == null) zona = "Europe/Madrid"; // Fallback
                if (dia == null) dia = java.time.LocalDate.now();

                // B. Llamada al Servicio (Probamos con el calendario 'primary')
                // Nota: En producción usarías la lista de calendarios guardada en BBDD,
                // aquí forzamos "primary" para verificar que la conexión funciona.
                List<String> calendariosAProbar = List.of("primary");

                areaResultados.setValue("Consultando Google API para " + anfitrion.getCorreo() + " el día " + dia + "...");

                List<RangoEvento> resultados = googleApiService.obtenerCalendariosGoogle(
                        token,
                        dia,
                        zona,
                        calendariosAProbar
                );
                EnlaceService enlace = new EnlaceService(disponibilidadRepository);
                enlace.calcularDisponibilidad(resultados,evento,dia);

                // C. Mostrar Resultados
                StringBuilder sb = new StringBuilder();
                sb.append("✅ ÉXITO. Se encontraron ").append(resultados.size()).append(" bloqueos:\n");

                if (resultados.isEmpty()) {
                    sb.append("-> El usuario está totalmente libre este día (0 eventos).");
                } else {
                    for (RangoEvento rango : resultados) {
                        sb.append("🔴 Ocupado: ")
                                .append(rango.getHoraInicio().toLocalTime())
                                .append(" - ")
                                .append(rango.getHoraFin().toLocalTime())
                                .append("\n");
                    }
                }
                areaResultados.setValue(sb.toString());

            } catch (Exception ex) {
                areaResultados.setValue("❌ EXCEPCIÓN: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Añadimos todo al layout
        derechaLayout.add(
                descripcionTexto,
                correo,
                fechaReunion,
                horaReunion,
                finalizar,
                new Hr(), // Separador visual
                tituloTest,
                btnTest,
                areaResultados
        );
        // ============================================================

        return derechaLayout;
    }
}