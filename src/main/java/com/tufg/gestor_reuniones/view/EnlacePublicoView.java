package com.tufg.gestor_reuniones.view;

import com.tufg.gestor_reuniones.model.Evento;
import com.tufg.gestor_reuniones.model.Usuario;
import com.tufg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tufg.gestor_reuniones.repository.EventoRepository;
import com.tufg.gestor_reuniones.service.EventoService;
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
    public EnlacePublicoView(EventoRepository eventoRepository, UsuarioService usuarioService, DisponibilidadRepository disponibilidadRepository ) {
        // Header
        setSizeFull();
        H1 cabecera = new H1("Gestor de Reuniones");
        cabecera.getElement().getStyle().set("text-align", "center");

        this.eventoService = new EventoService(eventoRepository, usuarioService , disponibilidadRepository);

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

    private VerticalLayout derechaLayout(){
        VerticalLayout derechaLayout = new VerticalLayout();
        derechaLayout.setWidthFull();
        derechaLayout.add(new H3("Detalles"));
        Span descripcionTexto = new Span(evento.getDescripcion());
        descripcionTexto.getStyle().set("white-space", "pre-wrap");
        derechaLayout.getStyle().set("background-color", "#F8F9FA");
        derechaLayout.getStyle().set("border-left", "1px solid #ddd");
        EmailField correo = new EmailField("Correo");

        DatePicker fechaReunión = new DatePicker("Fecha Reunión");
        TimePicker horaReunion = new TimePicker("Hora Reunión");

        Button finalizar = new Button("Finalizar");

        derechaLayout.add(descripcionTexto,correo,fechaReunión,horaReunion,finalizar);

        return derechaLayout;
    }


}