package com.tufg.gestor_reuniones.view;

import com.tufg.gestor_reuniones.model.Evento;
import com.tufg.gestor_reuniones.model.Usuario;
import com.tufg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tufg.gestor_reuniones.repository.EventoRepository;
import com.tufg.gestor_reuniones.service.EventoService;
import com.tufg.gestor_reuniones.service.UsuarioService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.List;


@Route(value = "home")
@PageTitle("home")
@PermitAll
public class HomeView extends VerticalLayout {
    public EventoService eventoService;
    public HomeView(EventoRepository eventoRepository, UsuarioService usuarioService, DisponibilidadRepository disponibilidadRepository ) {
        // Header
        setSizeFull();
        H1 cabecera = new H1("Gestor de Reuniones");
        cabecera.getElement().getStyle().set("text-align", "center");
        Button print = new Button(new Icon(VaadinIcon.PRINT));
        Button external = new Button(new Icon(VaadinIcon.EXTERNAL_LINK));
        external.addClickListener(e ->
                UI.getCurrent().getPage().open("https://vaadin.com", "_blank")
        );
        this.eventoService = new EventoService(eventoRepository, usuarioService , disponibilidadRepository);
        Button prueba = nuevaReunion();
        prueba.addClickListener(e -> {
            CrearReunionDialog dialog = new CrearReunionDialog(eventoRepository, usuarioService, disponibilidadRepository);
            dialog.open();
        });
        HorizontalLayout header = new HorizontalLayout(cabecera, prueba);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setFlexGrow(1, cabecera);   // ahora sí, con el componente
        header.setPadding(true);
        header.setSpacing(true);
        header.setWidthFull();
        header.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        header.addClassName("box-shadow-s"); // clase CSS propia
        add(header);
        // Content

        VerticalLayout content = new VerticalLayout(crearTabla());
        content.setPadding(true);
        content.setSizeFull();
        add(content);
        setFlexGrow(1, content);
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

   private Grid<Evento> crearTabla(){
       Grid<Evento> grid = new Grid<>(Evento.class, false);
       grid.setSizeFull();
       grid.addColumn(Evento::getNombre).setHeader("T\u00EDtulo");
       grid.addColumn(Evento::getDescripcion).setHeader("Descripci\u00F3n");
       grid.addColumn(Evento::getDuracion).setHeader("Duraci\u00F3n");
       grid.addComponentColumn(this::anfitriones).setHeader("Participantes");
       grid.addColumn(Evento::getEnlacePublico).setHeader("Enlace P\u00FAblico");


       List<Evento> evento = eventoService.obtenerTablaEvento();
       grid.setItems(evento);
       return grid;
   }
   private VerticalLayout anfitriones(Evento evento){
        VerticalLayout anfitrion = new VerticalLayout();
        anfitrion.setSpacing(false);
        anfitrion.setPadding(false);
        for(Usuario usuario: evento.getAnfitriones().stream().toList()){
            anfitrion.add(new Span(usuario.getCorreo()));
        }
        return anfitrion;
   }
}