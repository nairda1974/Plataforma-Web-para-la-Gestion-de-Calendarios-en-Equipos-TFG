package com.tfg.gestor_reuniones.view;

import com.tfg.gestor_reuniones.dto.RangoEvento;
import com.tfg.gestor_reuniones.model.Calendario;
import com.tfg.gestor_reuniones.model.Evento;
import com.tfg.gestor_reuniones.model.Usuario;
import com.tfg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tfg.gestor_reuniones.repository.EventoRepository;
import com.tfg.gestor_reuniones.service.ReunionService;
import com.tfg.gestor_reuniones.service.EventoService;
import com.tfg.gestor_reuniones.service.GoogleApiService;
import com.tfg.gestor_reuniones.service.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.*;
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
import jakarta.annotation.security.PermitAll;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Route(value = "reunion")
@PageTitle("reunion")
@PermitAll
@AnonymousAllowed
public class ReunionView extends VerticalLayout implements HasUrlParameter<String> {
    public EventoService eventoService;
    public Evento evento;
    private final GoogleApiService googleApiService;
    public DisponibilidadRepository disponibilidadRepository;
    public ReunionService reunionService;
    public String enlacePublico;
    public ReunionView(EventoRepository eventoRepository, UsuarioService usuarioService, DisponibilidadRepository disponibilidadRepository,
                       GoogleApiService googleApiService, ReunionService reunionService) {
        // Header
        setSizeFull();
        H1 cabecera = new H1("Gestor de Reuniones");
        cabecera.getElement().getStyle().set("text-align", "center");
        this.googleApiService = googleApiService;
        this.eventoService = new EventoService(eventoRepository, usuarioService, disponibilidadRepository);
        this.disponibilidadRepository = disponibilidadRepository;
        this.reunionService = reunionService;
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
        enlacePublico = "reunion/" + parameter;
        Optional<Evento> eventoOptional = eventoService.obtenerEvento(enlacePublico);
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
            mostrarError();
        }
    }

    private void mostrarError(){
        VerticalLayout rutaNoValidaLayout = new VerticalLayout();
        rutaNoValidaLayout.setSizeFull();
        rutaNoValidaLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        rutaNoValidaLayout.setAlignItems(Alignment.CENTER);

        H1 titulo = new H1("Reuni\u00F3n no v\u00E1lida");
        Span mensajeError = new Span("El enlace introducido es inv\u00E1lido");
        Button volverAlInicio = new Button("Volver al Inicio");
        volverAlInicio.addClickListener(error -> {getUI().ifPresent(ui -> ui.navigate("login"));});
        rutaNoValidaLayout.add(titulo,mensajeError,volverAlInicio);
        add(rutaNoValidaLayout);
    }

    private Button nuevaReunion() {
        Button nuevaReunnion = new Button("Crear Reuni\u00F3n", new Icon(VaadinIcon.FILE_ADD));
        nuevaReunnion.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nuevaReunnion.getStyle().set("margin-top", "var(--lumo-space-l)");
        nuevaReunnion.setIconAfterText(true);
        nuevaReunnion.getStyle().set("text-decoration", "none");
        nuevaReunnion.getStyle().set("display", "inline-flex");
        nuevaReunnion.getStyle().set("align-items", "center");
        nuevaReunnion.getStyle().set("justify-content", "center");

        return nuevaReunnion;
    }


    private VerticalLayout izquierdaLayout() {
        VerticalLayout izquierdaLayout = new VerticalLayout();
        izquierdaLayout.setWidthFull();
        String nombre = "";
        if (evento.getNombre() != null && evento.getNombre().length() > 1)
            nombre = evento.getNombre().substring(0, 1).toUpperCase() + evento.getNombre().substring(1);
        if (evento.getNombre() != null && evento.getNombre().length() == 1)
            nombre = evento.getNombre();

        H1 titulo = new H1(nombre);
        titulo.getStyle().set("color", "#0D47A1");
        H3 campoDuracion = new H3("Duraci\u00F3n: ");
        campoDuracion.getStyle().set("font-weight", "bold");
        campoDuracion.getStyle().set("color", "black");

        Span valorDuracion = new Span(" "+evento.getDuracion() + " min.");
        valorDuracion.getStyle().set("color", "#555555");

        H3 campoDescripcion = new H3("Descripci\u00F3n: ");
        campoDescripcion.getStyle().set("font-weight", "bold");
        campoDescripcion.getStyle().set("color", "black");

        Span valorDescripcion = new Span(" "+evento.getDescripcion());
        valorDescripcion.getStyle().set("color", "#333");

        VerticalLayout listadoParticipante = new VerticalLayout();
        listadoParticipante.setPadding(false);
        listadoParticipante.setSpacing(true);

        for (Usuario participante : evento.getAnfitriones()) {
            Span correo = new Span("- " + participante.getCorreo());
            listadoParticipante.add(correo);
        }
        HorizontalLayout descripcionLayout = new HorizontalLayout(campoDescripcion, valorDescripcion);
        descripcionLayout.setSpacing(true);
        descripcionLayout.setPadding(false);
        descripcionLayout.getStyle().set("flex-wrap", "wrap");
        descripcionLayout.setAlignItems(Alignment.BASELINE);

        HorizontalLayout duracionLayout = new HorizontalLayout(campoDuracion, valorDuracion);
        duracionLayout.setSpacing(true);
        duracionLayout.setPadding(false);
        duracionLayout.setAlignItems(Alignment.BASELINE);

        H2 tituloParticipantes = new H2("Participantes: ");
        tituloParticipantes.getStyle().set("color", "black");
        Scroller scroller = new Scroller(listadoParticipante);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.setHeight("200px");
        scroller.setWidthFull();
        izquierdaLayout.add(titulo, duracionLayout, descripcionLayout, tituloParticipantes, listadoParticipante, scroller);
        return izquierdaLayout;
    }

    private VerticalLayout derechaLayout() {
        VerticalLayout derechaLayout = new VerticalLayout();
        derechaLayout.setWidthFull();
        derechaLayout.setAlignItems(Alignment.END);
        Button botonCrearReunion = new Button("Crear Reunion");
        botonCrearReunion.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span descripcionTexto = new Span(evento.getDescripcion());
        descripcionTexto.getStyle().set("white-space", "pre-wrap");
        derechaLayout.getStyle().set("background-color", "#F8F9FA");
        derechaLayout.getStyle().set("border-left", "1px solid #ddd");
        ComboBox husoHorario = desplegableZonaHoraria();
        EmailField correo = new EmailField("Correo");
        correo.setWidthFull();
        ComboBox<LocalDateTime> horaReunion = new ComboBox<>("Horarios disponibles");
        horaReunion.setWidthFull();
        DatePicker fechaReunion = new DatePicker("Fecha Reuni\u00F3n");
        fechaReunion.setMin(LocalDate.now()); // La fecha minima es la del dia actual
        horaReunion.setEnabled(false);
        horaReunion.setRequired(true);
        horaReunion.setErrorMessage("Es obligatorio seleccionar una hora para realizar la reuni\u00F3n");
        fechaReunion.setWidthFull();
        horaReunion.setPlaceholder("Rellene el huso horario y la fecha de la reuni\u00F3n");
        horaReunion.setItemLabelGenerator(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        fechaReunion.addValueChangeListener(e ->{
            boolean habilitado = habilitarHoraReunion(husoHorario,fechaReunion,horaReunion);
            if (habilitado) {
                List<LocalDateTime> horario = horarioDisponible( husoHorario,  fechaReunion,  evento);
                if(horario != null && !horario.isEmpty())
                    horaReunion.setItems(horario);
                else
                    Notification.show("No hay hora disponible, por favor seleccione otro dia");
            }

        });
        fechaReunion.setHelperText("Solo se puede crear la reuni\u00F3n en uno de los siguientes dias: "+ reunionService.obtenerDiaDisponible(evento));
        fechaReunion.setRequired(true);
        fechaReunion.setErrorMessage("Es obligatoria seleccionar una fecha para realizar la reuni\u00F3n");
        String diaSemanaDisponible = reunionService.obtenerDiaDisponible(evento);
        husoHorario.addValueChangeListener(e ->{
            boolean habilitado = habilitarHoraReunion(husoHorario,fechaReunion,horaReunion);
            if (habilitado) {
                List<LocalDateTime> horario = horarioDisponible( husoHorario,  fechaReunion,  evento);
                if(horario != null && !horario.isEmpty())
                    horaReunion.setItems(horario);
                else
                    Notification.show("No hay hora disponible, por favor seleccione otro dia");
            }
        });
        correo.setRequired(true);
        correo.setErrorMessage("Es obligatorio introducir un correo electr\u00F3nico v\u00E1lido.");
        botonCrearReunion.addClickListener(e -> {almacenarReunionGoogleCalendar( correo, husoHorario,fechaReunion, horaReunion);});
        derechaLayout.add(husoHorario, correo, fechaReunion, horaReunion, botonCrearReunion);
        return derechaLayout;
    }



    private void almacenarReunionGoogleCalendar(EmailField correo, ComboBox<?> husoHorario,
                                                DatePicker fechaReunion, ComboBox<LocalDateTime> horaReunion){
        if(validarAlmacenarReunionprivate(correo,husoHorario , fechaReunion, horaReunion))
            return;

        if(!horarioDisponible( husoHorario,  fechaReunion,  evento).contains(horaReunion.getValue())){
            Notification.show("La hora seleccionada no est\u00E1 disponible seleccione otra hora de reuni\u00F3n.");
            return;
        }
        String refreshToken = evento.getCreador().getGoogleRefreshToken();

        List<String> listaAnfitriones = new ArrayList<>();

        for (Usuario anfitrion : evento.getAnfitriones()) {
            listaAnfitriones.add(anfitrion.getCorreo());
        }
        List<String> listaParticipantes = googleApiService.obtenerListaCorreoGoogle(listaAnfitriones);
        listaParticipantes.add(correo.getValue()); // El correo del invitado que crea la reunion

        String nombreTitulo = "Reuni\u00F3n: " + evento.getNombre() ;
        String descripcion = evento.getDescripcion();

        googleApiService.almacenarReunionCalendario(
                refreshToken,
                horaReunion.getValue(),
                husoHorario.getValue().toString(),
                evento.getDuracion(),
                listaParticipantes,
                nombreTitulo,
                descripcion
        );
        reunionService.almacenarReunion(correo.getValue(),horaReunion.getValue(), enlacePublico);
    }





    private boolean validarAlmacenarReunionprivate(EmailField correo, ComboBox<?> husoHorario,
                                                   DatePicker fechaReunion, ComboBox<LocalDateTime> horaReunion) {
        boolean error = false;
        if (correo.isEmpty() || correo.isInvalid()) {
            Notification.show("Es obligatorio rellenar un correo electr\u00F3nico.");
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
            Notification.show("Es obligatorio seleccionar una fecha de reuni\u00F3n.");

        }

        if (horaReunion.isEmpty()) {
            horaReunion.setInvalid(true);
            error = true;
            Notification.show("Es obligatorio seleccionar la hora en la que se realizar\u00E1 la reuni\u00F3n.");

        }
        return error;
    }

    private boolean  habilitarHoraReunion(ComboBox husoHorario , DatePicker fechaReunion,ComboBox<LocalDateTime> horaReunion ){
        boolean husoHorarioConValor = !husoHorario.isEmpty() && husoHorario.getValue() != null;
        boolean fechaReunionConValor = !fechaReunion.isEmpty() && fechaReunion.getValue() != null;
        horaReunion.setEnabled(husoHorarioConValor && fechaReunionConValor);
        if(husoHorarioConValor && fechaReunionConValor) {
            horaReunion.setPlaceholder("--Seleccione una opci\u00F3n--");
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
                listadoCalendario.add(calendario.getIdentificadorNombreCalendario());
            eventosTotales.addAll(googleApiService.obtenerCalendariosGoogle(usuario.getGoogleRefreshToken(), fechaReunion.getValue(), husoHorario.getValue().toString(), listadoCalendario));

        }
        return eventosTotales;
    }
    private List<LocalDateTime> horarioDisponible(ComboBox husoHorario, DatePicker fechaReunion, Evento evento)  {
        List<RangoEvento> eventosParticipantes = eventosParticipantes(husoHorario,fechaReunion);
        return reunionService.calcularDisponibilidad(eventosParticipantes, evento, fechaReunion.getValue(),(String)husoHorario.getValue());
    }



    private ComboBox desplegableZonaHoraria() {
        List<String> zonas = ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        ComboBox<String> zonaHorariaDesplegable = new ComboBox<>("Zona horaria");
        zonaHorariaDesplegable.setItems(zonas);
        zonaHorariaDesplegable.setValue(ZoneId.systemDefault().getId());
        zonaHorariaDesplegable.setWidthFull();
        zonaHorariaDesplegable.setRequired(true);
        zonaHorariaDesplegable.setErrorMessage("Es obligatorio seleccionar una zona horaria.");
        return zonaHorariaDesplegable;
    }
}