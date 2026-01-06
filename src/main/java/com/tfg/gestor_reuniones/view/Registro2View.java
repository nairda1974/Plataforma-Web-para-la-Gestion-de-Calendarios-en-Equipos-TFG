package com.tfg.gestor_reuniones.view;

import com.tfg.gestor_reuniones.model.Calendario;
import com.tfg.gestor_reuniones.model.Usuario;
import com.tfg.gestor_reuniones.service.GoogleApiService;
import com.tfg.gestor_reuniones.service.UsuarioService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Route("registro2")
@PageTitle("Registro")
@AnonymousAllowed
public class Registro2View extends VerticalLayout {
    private MultiSelectComboBox<String> depslegableCalendario;
    private ComboBox<String> zonaHorariaDesplegable;
    private final HttpServletRequest request;
    private GoogleApiService googleApiService;
    private UsuarioService usuarioService;
    private PasswordEncoder cifrador;
    private List<Calendario> listaCalendario;

    public Registro2View(GoogleApiService googleApiService,HttpServletRequest request, UsuarioService usuarioService,PasswordEncoder cifrador) {
        this.request = request;
        this.googleApiService = googleApiService;
        this.usuarioService = usuarioService;
        this.cifrador = cifrador;
        listaCalendario = new ArrayList<>();
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#eef2f6");
        VerticalLayout registro2Layout = new VerticalLayout();
        registro2Layout.getStyle().set("background-color", "white");
        registro2Layout.getStyle().set("border-radius", "8px");
        registro2Layout.getStyle().set("border", "1px solid #dbdbdb");
        registro2Layout.getStyle().set("box-shadow", "5px 5px 15px rgba(0,0,0,0.1)");

        registro2Layout.setWidth("100%");
        registro2Layout.setMaxWidth("450px");
        registro2Layout.setPadding(true);
        registro2Layout.setSpacing(false);
        registro2Layout.setAlignItems(Alignment.CENTER);
        registro2Layout.getStyle().set("text-align", "center");


        this.depslegableCalendario = desplegableCalendario();
        this.zonaHorariaDesplegable = desplegableZonaHoraria();

        Icon icono = new Icon(VaadinIcon.CALENDAR_CLOCK);
        icono.setSize("50px");
        icono.setColor("#0D47A1");
        icono.getStyle().set("margin-bottom", "15px");
        // Titulo registrar usuario header
        H1 titulo = new H1("Registrar Usuario ");
        Paragraph subtitulo = new Paragraph("Cree su cuenta en el Gestor de Reuniones 2/2");
        subtitulo.getStyle().set("color", "#666666");
        subtitulo.getStyle().set("font-size", "14px");
        subtitulo.getStyle().set("margin-top", "0");
        subtitulo.getStyle().set("margin-bottom", "20px");
        // Añadir mensaje inverso de registro
        HorizontalLayout enlace = new HorizontalLayout();
        enlace.setJustifyContentMode(JustifyContentMode.CENTER);
        enlace.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        enlace.setWidthFull();
        enlace.setSpacing(true);
        enlace.getStyle().set("font-size", "12px");
        enlace.getStyle().set("margin-top", "20px");

        Span textoRegistrado = new Span("¿Ya estás registrado?");
        textoRegistrado.getStyle().set("color", "gray");

        RouterLink enlaceLogin = new RouterLink("Inicia sesión", LoginView.class);

        enlace.add(textoRegistrado, enlaceLogin);
        // Añadir todos los elementos principales
        registro2Layout.add(icono,titulo, subtitulo,zonaHorariaDesplegable, depslegableCalendario, siguienteBoton(), enlace);
        add(registro2Layout);
    }

    private MultiSelectComboBox<String> desplegableCalendario(){
        String refreshToken = (String) request.getSession().getAttribute("refreshToken");
        this.listaCalendario = googleApiService.listadoGoogleCalendar(refreshToken);
        MultiSelectComboBox<String> calendarioDesplegable = new MultiSelectComboBox<>("Selecciona tus calendarios");
        List<String> calendarioNombre = new ArrayList<>();
        for(Calendario calendario : listaCalendario){
            calendarioNombre.add(calendario.getNombreCalendario());
        }
        calendarioDesplegable.setItems(calendarioNombre);

        if (listaCalendario != null && !listaCalendario.isEmpty()) {
            calendarioDesplegable.select(calendarioNombre.get(0));
        }
        calendarioDesplegable.setWidthFull();
        calendarioDesplegable.setRequired(true);
        calendarioDesplegable.setErrorMessage("Es obligatorio introducir al menos un calendario.");
        return calendarioDesplegable;
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

    private Button siguienteBoton() {
        Button siguiente = new Button("Crear Usuario", new Icon(VaadinIcon.ARROW_RIGHT));
        siguiente.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        siguiente.getStyle().set("margin-top", "20px");
        siguiente.setIconAfterText(true);

        siguiente.setWidthFull();
        return validacionesBotonSiguiente(siguiente);
    }

    private Button validacionesBotonSiguiente( Button siguiente){
        siguiente.addClickListener(e -> {
            boolean error = false;
            if(depslegableCalendario.isEmpty()){
                error = true;
                //Notification.show("Es obligatorio seleccionar al menos un calendario.");
            }
            if(zonaHorariaDesplegable.isEmpty()){
                error = true;
                //Notification.show("Es obligatorio seleccionar tu zona horaria.");
            }

            String correo = (String) request.getSession().getAttribute("correo");
            String contrasenia = (String) request.getSession().getAttribute("contrasenia");

            if (correo == null || contrasenia == null) {
                Notification.show("Se ha caducado la sesión.");
                UI.getCurrent().navigate("registro");
                return;
            }
            if (error)
                return;

            String refreshToken = (String) request.getSession().getAttribute("refreshToken");
            Usuario usuario = setUsuario(correo, contrasenia, depslegableCalendario.getValue());
            usuario.setGoogleRefreshToken(refreshToken);
            usuarioService.registrarUsuario(usuario);
            getUI().ifPresent(ui -> {
                ui.getSession().close();
                ui.getPage().setLocation("/logout");
            });
        });
        return siguiente;
    }

    private Usuario setUsuario(String correo, String contrasenia, Set<String> listaCalendarios){
        Usuario usuario = new Usuario();
        usuario.setContrasenia(cifrador.encode(contrasenia));
        usuario.setCorreo(correo);
        usuario.setHusoHorario(zonaHorariaDesplegable.getValue().toString());
        List<Calendario> listadeCalendario = new ArrayList<>();
        for(Calendario calendar: this.listaCalendario) {
            if(listaCalendarios.contains(calendar.getNombreCalendario())){
                Calendario calendario = new Calendario();
                calendario.setUsuario(usuario);
                calendario.setNombreCalendario(calendar.getNombreCalendario());
                calendario.setIdentificadorNombreCalendario(calendar.getIdentificadorNombreCalendario());
                listadeCalendario.add(calendario);
            }
        }
        usuario.setCalendarios(listadeCalendario);
        return usuario;
    }
}