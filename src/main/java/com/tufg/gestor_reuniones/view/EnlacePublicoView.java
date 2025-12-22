package com.tufg.gestor_reuniones.view;

import com.tufg.gestor_reuniones.dto.RangoEvento;
import com.tufg.gestor_reuniones.model.Calendario;
import com.tufg.gestor_reuniones.model.Evento;
import com.tufg.gestor_reuniones.model.Usuario;
import com.tufg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tufg.gestor_reuniones.repository.EventoRepository;
import com.tufg.gestor_reuniones.service.EnlaceService;
import com.tufg.gestor_reuniones.service.EventoService;
import com.tufg.gestor_reuniones.service.GoogleApiService;
import com.tufg.gestor_reuniones.service.UsuarioService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Route(value = "reunion")
@PageTitle("reunion")
@PermitAll
@AnonymousAllowed
public class EnlacePublicoView extends VerticalLayout implements HasUrlParameter<String> {
    public EventoService eventoService;
    public Evento evento;
    private final GoogleApiService googleApiService;
    public DisponibilidadRepository disponibilidadRepository;
    public EnlaceService enlaceService;
    public EnlacePublicoView(EventoRepository eventoRepository, UsuarioService usuarioService, DisponibilidadRepository disponibilidadRepository,
                             GoogleApiService googleApiService, EnlaceService enlaceService) {
        // Header
        setSizeFull();
        H1 cabecera = new H1("Gestor de Reuniones");
        cabecera.getElement().getStyle().set("text-align", "center");
        this.googleApiService = googleApiService;
        this.eventoService = new EventoService(eventoRepository, usuarioService, disponibilidadRepository);
        this.disponibilidadRepository = disponibilidadRepository;
        this.enlaceService = enlaceService;
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
        Optional<Evento> eventoOptional = eventoService.obtenerEvento("reunion/" + parameter);
        if (eventoOptional.isPresent()) {
            evento = eventoOptional.get();
            VerticalLayout izquierda = izquierdaLayout();
            VerticalLayout derecha = derechaLayout();
            izquierda.setWidth("40%");
            derecha.setWidth("60%");
            HorizontalLayout tarjeta = new HorizontalLayout(izquierda, derecha);
            tarjeta.setSizeFull();
            add(tarjeta);
            setFlexGrow(1, tarjeta);
        } else {
            System.out.println("Error no se ha podido recuperar el evento");
        }
    }

    private Button nuevaReunion() {
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


    private VerticalLayout izquierdaLayout() {
        VerticalLayout izquierdaLayout = new VerticalLayout();
        izquierdaLayout.setWidthFull();
        String nombre = "";
        if (evento.getNombre() != null && evento.getNombre().length() > 1)
            nombre = evento.getNombre().substring(0, 1).toUpperCase() + evento.getNombre().substring(1);
        HorizontalLayout titulo = new HorizontalLayout();
        H2 nombreCampo = new H2(nombre);
        nombreCampo.getStyle().set("color", "#1e3a8a");
        Icon calendarioIcono = new Icon(VaadinIcon.CALENDAR);
        calendarioIcono.setColor("#1e3a8a");
        titulo.add(calendarioIcono, nombreCampo);
        // representa la duracion de la reunion
        HorizontalLayout duracionLayout = new HorizontalLayout();
        Span duracion = new Span(evento.getDuracion() + " minutos");
        duracion.getStyle().set("white-space", "pre-wrap");
        duracion.getStyle().set("color", "#444");
        duracionLayout.add(new Icon(VaadinIcon.USER_CLOCK), duracion);

        // Da una descripcion mas detallada sobre la reunion
        H3 descripcionCampo = new H3("Detalles");

        Span descripcion = new Span(evento.getDescripcion());
        descripcion.getStyle().set("white-space", "pre-wrap");
        descripcion.getStyle().set("color", "#444");

        VerticalLayout listadoParticipante = new VerticalLayout();
        H3 participantesCampo = new H3("Participantes");

        // Listado con todos los participantes
        for (Usuario participante : evento.getAnfitriones()) {
            Avatar iconoUsuario = new Avatar(participante.getCorreo());
            Span correo = new Span(participante.getCorreo());
            correo.getStyle().set("white-space", "pre-wrap");
            correo.getStyle().set("color", "#444");
            HorizontalLayout participanteLayout = new HorizontalLayout();
            participanteLayout.add(iconoUsuario, correo);
            listadoParticipante.add(participanteLayout);
        }
        Scroller scroller = new Scroller(listadoParticipante);
        scroller.addClassNames(LumoUtility.Border.BOTTOM, LumoUtility.Padding.MEDIUM);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.setMaxHeight("20hv");
        izquierdaLayout.add(titulo, duracionLayout, descripcionCampo, descripcion, participantesCampo, scroller);
        return izquierdaLayout;
    }

    private VerticalLayout derechaLayout() {
        VerticalLayout derechaLayout = new VerticalLayout();
        derechaLayout.setWidthFull();
        Button botonCrearReunion = new Button("Crear Reunion");
        botonCrearReunion.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        derechaLayout.add(new H3("Detalles"));
        Span descripcionTexto = new Span(evento.getDescripcion());
        descripcionTexto.getStyle().set("white-space", "pre-wrap");
        derechaLayout.getStyle().set("background-color", "#F8F9FA");
        derechaLayout.getStyle().set("border-left", "1px solid #ddd");
        ComboBox husoHorario = desplegableZonaHoraria();
        EmailField correo = new EmailField("Correo");

        ComboBox<LocalTime> horaReunion = new ComboBox<>("Horarios disponibles");
        DatePicker fechaReunion = new DatePicker("Fecha Reunión");
        fechaReunion.setMin(LocalDate.now()); // La fecha minima es la del dia actual
        horaReunion.setEnabled(false);
        horaReunion.setPlaceholder("Rellene el huso horario y la fecha de la reunión");
        horaReunion.setItemLabelGenerator(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        fechaReunion.addValueChangeListener(e ->{
            boolean habilitado = habilitarHoraReunion(husoHorario,fechaReunion,horaReunion);
            if (habilitado) {
                horaReunion.setItems(horarioDisponible( husoHorario,  fechaReunion,  evento));
            }

        });
        husoHorario.addValueChangeListener(e ->{
            boolean habilitado = habilitarHoraReunion(husoHorario,fechaReunion,horaReunion);
            if (habilitado) {
                horaReunion.setItems(horarioDisponible( husoHorario,  fechaReunion,  evento));
            }
        });
        correo.setRequired(true);
        correo.setErrorMessage("Es obligatorio introducir un correo electrónico válido.");
        botonCrearReunion.addClickListener(e -> {almacenarReunionGoogleCalendar( correo, husoHorario,fechaReunion, horaReunion);});
        derechaLayout.add(new H3("Detalles"), husoHorario, correo, fechaReunion, horaReunion, botonCrearReunion);
        return derechaLayout;
    }

    private void almacenarReunionGoogleCalendar(EmailField correo, ComboBox<?> husoHorario,
                                                DatePicker fechaReunion, ComboBox<LocalTime> horaReunion){
        if(validarAlmacenarReunionprivate(correo,husoHorario , fechaReunion, horaReunion))
            return;

        if(!horarioDisponible( husoHorario,  fechaReunion,  evento).contains(horaReunion.getValue())){
            Notification.show("La hora seleccionada no está disponible seleccione otra hora de reunión.");
            return;
        }
        LocalDateTime fechaHoraReunion = LocalDateTime.of(fechaReunion.getValue(), horaReunion.getValue());
        String refreshToken = evento.getCreador().getGoogleRefreshToken();

        List<String> correosParticipantes = new ArrayList<>();
        correosParticipantes.add(correo.getValue()); // El correo del invitado que crea la reunion

        for (Usuario anfitrion : evento.getAnfitriones()) {
            correosParticipantes.add(anfitrion.getCorreo());
        }

        String nombreTitulo = "Reunión: " + evento.getNombre() ;
        String descripcion = evento.getDescripcion();

        googleApiService.almacenarReunionCalendario(
                refreshToken,
                fechaHoraReunion,
                husoHorario.getValue().toString(),
                evento.getDuracion(),
                correosParticipantes,
                nombreTitulo,
                descripcion
        );
    }





    private boolean validarAlmacenarReunionprivate(EmailField correo, ComboBox<?> husoHorario,
                                                   DatePicker fechaReunion, ComboBox<LocalTime> horaReunion) {
        boolean error = false;
        if (correo.isEmpty() || correo.isInvalid()) {
            Notification.show("Es obligatorio rellenar un correo electrónico.");
            correo.setInvalid(true);
            error = true;
        }

        if (husoHorario.isEmpty()) {
            Notification.show("Es obligatorio seleccionar un huso horario.");
            husoHorario.setInvalid(true);
            error = true;
        }

        if (fechaReunion.isEmpty()) {
            fechaReunion.setInvalid(true);
            error = true;
            Notification.show("Es obligatorio seleccionar una fecha de reunión.");

        }

        if (horaReunion.isEmpty()) {
            horaReunion.setInvalid(true);
            error = true;
            Notification.show("Es obligatorio seleccionar la hora en la que se realizará la reunión.");

        }
        return error;
    }

    private boolean  habilitarHoraReunion(ComboBox husoHorario , DatePicker fechaReunion,ComboBox<LocalTime> horaReunion ){
        boolean husoHorarioConValor = !husoHorario.isEmpty() && husoHorario.getValue() != null;
        boolean fechaReunionConValor = !fechaReunion.isEmpty() && fechaReunion.getValue() != null;
        horaReunion.setEnabled(husoHorarioConValor && fechaReunionConValor);
        if(husoHorarioConValor && fechaReunionConValor) {
            horaReunion.setPlaceholder("--Seleccione una opción--");
        }
        return husoHorarioConValor && fechaReunionConValor;
    }

    private List<RangoEvento> eventosParticipantes(ComboBox husoHorario, DatePicker fechaReunion)  {
        List<RangoEvento> eventosTotales = new ArrayList<>();
        List<String> listadoCalendarioCreador = new ArrayList<>();
        for (Calendario calendario : evento.getCreador().getCalendarios())
            listadoCalendarioCreador.add(calendario.getNombreCalendario());
        eventosTotales.addAll(googleApiService.obtenerCalendariosGoogle(evento.getCreador().getGoogleRefreshToken(), fechaReunion.getValue(), husoHorario.getValue().toString(), listadoCalendarioCreador));
        for (Usuario usuario : evento.getAnfitriones()) {
            List<String> listadoCalendario = new ArrayList<>();
            for (Calendario calendario : usuario.getCalendarios())
                listadoCalendario.add(calendario.getNombreCalendario());
            eventosTotales.addAll(googleApiService.obtenerCalendariosGoogle(usuario.getGoogleRefreshToken(), fechaReunion.getValue(), husoHorario.getValue().toString(), listadoCalendario));

        }
        return eventosTotales;
    }
    private List<LocalTime> horarioDisponible(ComboBox husoHorario, DatePicker fechaReunion, Evento evento)  {
        List<RangoEvento> eventosParticipantes = eventosParticipantes(husoHorario,fechaReunion);
        return enlaceService.calcularDisponibilidad(eventosParticipantes, evento, fechaReunion.getValue());
    }



    private ComboBox desplegableZonaHoraria() {
        List<String> zonas = ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        ComboBox<String> zonaHorariaDesplegable = new ComboBox<>("Zona horaria");
        zonaHorariaDesplegable.setItems(zonas);
        zonaHorariaDesplegable.setValue(ZoneId.systemDefault().getId());
        return zonaHorariaDesplegable;
    }
}