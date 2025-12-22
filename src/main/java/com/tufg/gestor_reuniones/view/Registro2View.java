package com.tufg.gestor_reuniones.view;

import com.tufg.gestor_reuniones.model.Usuario;
import com.tufg.gestor_reuniones.service.GoogleApiService;
import com.tufg.gestor_reuniones.service.UsuarioService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.component.dependency.CssImport; // <-- Importante
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Route("registro2")
@PageTitle("Iniciar Sesión")
@AnonymousAllowed
@CssImport("./styles/login-view.css")
public class Registro2View extends VerticalLayout {
    private FormLayout registrationForm = new FormLayout();;


    private MultiSelectComboBox depslegableCalendario;
    private ComboBox zonaHorariaDesplegable;
    private final HttpServletRequest request;
    private GoogleApiService googleApiService;
    private UsuarioService usuarioService;
    private PasswordEncoder contraseniaEncoder;


    public Registro2View(GoogleApiService googleApiService,HttpServletRequest request, UsuarioService usuarioService,PasswordEncoder contraseniaEncoder) {
        this.request = request;
        this.googleApiService = googleApiService;
        this.usuarioService = usuarioService;
        this.contraseniaEncoder = contraseniaEncoder;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(45deg, #A7B6E9 0%, #E9EBF9 100%)");
        VerticalLayout loginLayout =  estiloLogin(); // Setea las propiedades css para que el login tenga un estilo similar a una tarjeta

        // Icono del calendario
        Icon icon = new Icon(VaadinIcon.CALENDAR_CLOCK); // Icono relacionado con reuniones
        icon.setSize("50px");
        icon.setColor("var(--lumo-primary-color)");
        icon.getStyle().set("margin-bottom", "var(--lumo-space-s)");



        this.depslegableCalendario = desplegableCalendario();
        this.zonaHorariaDesplegable = desplegableZonaHoraria();



        // Titulo registrar usuario header
        H1 title = new H1("Registrar Usuario 2/2");
        title.addClassName("login-title");
        registrationForm.getElement().executeJs("this.shadowRoot.querySelector('[part=\"header\"]')?.remove();");
        registrationForm.getStyle().set("padding", "1");
        registrationForm.getStyle().set("box-shadow", "none");
        registrationForm.add(zonaHorariaDesplegable, depslegableCalendario, siguienteBoton());
// Añadir mensaje inverso de registro
        HorizontalLayout registradoLayout = new HorizontalLayout();
        registradoLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        registradoLayout.setSpacing(true);
        registradoLayout.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("margin-top", "var(--lumo-space-m)");

        Span yaRegistradoText = new Span("¿Ya estás registrado?");
        yaRegistradoText.getStyle().set("color", "var(--lumo-contrast-70pct)");

        Anchor loginLink = new Anchor("login", "Inicia sesión");

        registradoLayout.add(yaRegistradoText, loginLink);
        // Añadir todos los elementos principales
        loginLayout.add(icon, title, registrationForm);
        loginLayout.add(registradoLayout);
        add(loginLayout);
    }

    private MultiSelectComboBox desplegableCalendario(){
        String accessToken = (String) request.getSession().getAttribute("accessToken");
        List<String> listaCalendario = googleApiService.listadoGoogleCalendar(accessToken);
        MultiSelectComboBox<String> calendarioDesplegable = new MultiSelectComboBox<>("Selecciona tus calendarios");
        calendarioDesplegable.setItems(listaCalendario);
        calendarioDesplegable.select(listaCalendario.get(0));
        calendarioDesplegable.setWidthFull();
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
        return zonaHorariaDesplegable;
    }



    private Button siguienteBoton() {
        Button siguiente = new Button("Crear Usuario", new Icon(VaadinIcon.ARROW_RIGHT));
        siguiente.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        siguiente.getStyle().set("margin-top", "var(--lumo-space-l)");
        siguiente.setIconAfterText(true);
        siguiente.getStyle()
                .set("text-decoration", "none")
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        siguiente.setWidthFull();
        return validacionesBotonSiguiente(siguiente);
    }

    private Button validacionesBotonSiguiente( Button siguiente){
        siguiente.addClickListener(e -> {
            boolean error = false;
            if(depslegableCalendario.isEmpty()){
                error = true;
                Notification.show("Es obligatorio seleccionar al menos un calendario.");
            }
            if(zonaHorariaDesplegable.isEmpty()){
                error = true;
                Notification.show("Es obligatorio seleccionar tu zona horaria.");
            }

            String correo = (String) request.getSession().getAttribute("correo");
            String contrasenia = (String) request.getSession().getAttribute("contrasenia");

            if (correo == null || contrasenia == null) {
                Notification.show("Se ha caducado la sesión.", 3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("registro");
                return;
            }
            if (error)
                return;

            String refreshToken = (String) request.getSession().getAttribute("refreshToken");
            Usuario usuario = setUsuario(correo, contrasenia);
            usuario.setGoogleRefreshToken(refreshToken);
            usuarioService.registrarUsuario(usuario);
            getUI().ifPresent(ui -> ui.navigate("logout"));
        });
        return siguiente;
    }




    private Usuario setUsuario(String correo, String contrasenia){
        Usuario usuario = new Usuario();
        usuario.setContrasenia(contraseniaEncoder.encode(contrasenia));
        usuario.setCorreo(correo);
        usuario.setHusoHorario(zonaHorariaDesplegable.getValue().toString());
        return usuario;
    }

    private VerticalLayout estiloLogin(){
        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.setAlignItems(Alignment.CENTER);
        loginLayout.getStyle().set("background-color", "var(--lumo-base-color)");
        loginLayout.getStyle().set("border-radius", "var(--lumo-border-radius-l)");
        loginLayout.getStyle().set("box-shadow", "var(--lumo-box-shadow-m)");
        loginLayout.getStyle().set("padding", "var(--lumo-space-xl)");
        loginLayout.setMaxWidth("620px"); // Un poco más de ancho para que respire
        loginLayout.setSpacing(false); // Controlamos el espacio manualmente
        loginLayout.setPadding(true);
        return loginLayout;
    }

}