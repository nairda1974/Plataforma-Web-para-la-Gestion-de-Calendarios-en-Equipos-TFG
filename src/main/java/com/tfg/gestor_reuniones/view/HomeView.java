package com.tfg.gestor_reuniones.view;

import com.tfg.gestor_reuniones.model.Evento;
import com.tfg.gestor_reuniones.model.Usuario;
import com.tfg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tfg.gestor_reuniones.repository.EventoRepository;
import com.tfg.gestor_reuniones.service.EventoService;
import com.tfg.gestor_reuniones.service.UsuarioService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;


@Route(value = "")
@PageTitle("home")
@PermitAll
public class HomeView extends VerticalLayout {
    @Autowired
    public EventoService eventoService;
    @Value("${cabecera.enlace.publico}")
    private String cabeceraEnlacePublico;
    public HomeView(EventoRepository eventoRepository, UsuarioService usuarioService, DisponibilidadRepository disponibilidadRepository,EventoService eventoService ) {
        setSizeFull();
        H1 cabecera = new H1("Gestor de Reuniones");
        cabecera.getElement().getStyle().set("text-align", "center");

        this.eventoService = eventoService;
        Button logout = new Button("Cerrar sesi\u00F3n");
        // Ejemplo basado en stack overflow https://stackoverflow.com/questions/71707714/vaadin-v22-how-does-one-properly-logout-of-a-session
        logout.addClickListener(e -> {
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().getPage().reload();
        });
        Button evento = nuevaReunion();
        evento.addClickListener(e -> {
            CrearEventoDialog dialog = new CrearEventoDialog(eventoRepository, usuarioService, disponibilidadRepository,eventoService);
            dialog.open();
        });
        HorizontalLayout header = new HorizontalLayout(logout,cabecera, evento);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setFlexGrow(1, cabecera);
        header.setPadding(true);
        header.setSpacing(true);
        header.setWidthFull();
        header.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        header.addClassName("box-shadow-s");
        add(header);
        VerticalLayout content = new VerticalLayout(crearTabla());
        content.setPadding(true);
        content.setSizeFull();
        add(content);
        setFlexGrow(1, content);
    }
    private Button nuevaReunion(){
        Button nuevaReunnion = new Button("Crear Reuni\u00F3n", new Icon(VaadinIcon.FILE_ADD));
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
       grid.addColumn(evento -> evento.getDuracion() + " minutos").setHeader("Duraci\u00F3n");
       grid.addComponentColumn(this::anfitriones).setHeader("Participantes");
       grid.addColumn(evento -> cabeceraEnlacePublico + evento.getEnlacePublico()).setHeader("Enlace P\u00FAblico");
       // Script de copia basado en https://cookbook.vaadin.com/copy-to-clipboard

       grid.addComponentColumn(e -> {
                   Button botonCopia = new Button("Copy to clipboard", VaadinIcon.COPY.create());
                   String enlace = cabeceraEnlacePublico + e.getEnlacePublico();

                   botonCopia.addClickListener(copia -> {

                       UI.getCurrent().getPage().executeJs(
                               "const textarea = document.createElement('textarea');" +
                                       "textarea.value = $0;" + // $0 es el primer argumento (enlace)
                                       "document.body.appendChild(textarea);" +
                                       "textarea.select();" +
                                       "document.execCommand('copy');" +
                                       "document.body.removeChild(textarea);",
                               enlace
                       );

                       Notification.show("Enlace copiado al portapapeles");
                   });
                   return botonCopia;
               }
       );

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