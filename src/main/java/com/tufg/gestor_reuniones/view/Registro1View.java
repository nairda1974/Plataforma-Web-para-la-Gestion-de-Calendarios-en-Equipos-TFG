package com.tufg.gestor_reuniones.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.component.dependency.CssImport; // <-- Importante
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Route("registro")
@PageTitle("Iniciar Sesión")
@AnonymousAllowed
@CssImport("./styles/login-view.css")
public class Registro1View extends VerticalLayout {
    private FormLayout registrationForm = new FormLayout();;
    private PasswordField contrasenia1;
    private PasswordField contrasenia2;
    private TextField correoElectronico;
    private final HttpServletRequest request;


    public Registro1View(HttpServletRequest request) {
        this.request = request;
        contrasenia1 = contraseniaCampo("Contraseña");
        contrasenia2 =  contraseniaCampo("Confirmar Contraseña");
        correoElectronico = correoCampo();

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

        // Titulo registrar usuario header
        H1 title = new H1("Registrar Usuario 1/2");
        title.addClassName("login-title");
        registrationForm.getElement().executeJs("this.shadowRoot.querySelector('[part=\"header\"]')?.remove();");
        registrationForm.getStyle().set("padding", "1");
        registrationForm.getStyle().set("box-shadow", "none");
        registrationForm.add(botonGoogle(), correoElectronico, contrasenia1, contrasenia2  , sigueinteBoton());
// Añadir mensaje inverso de registro
        HorizontalLayout yaRegistradoLayout = new HorizontalLayout();
        yaRegistradoLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        yaRegistradoLayout.setSpacing(true);
        yaRegistradoLayout.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("margin-top", "var(--lumo-space-m)");

        Span yaRegistradoText = new Span("¿Ya estás registrado?");
        yaRegistradoText.getStyle().set("color", "var(--lumo-contrast-70pct)");

        Anchor loginLink = new Anchor("login", "Inicia sesión");

        yaRegistradoLayout.add(yaRegistradoText, loginLink);
        // Añadir todos los elementos principales
        loginLayout.add(icon, title, registrationForm);
        loginLayout.add(yaRegistradoLayout);
        add(loginLayout);
    }
    private Anchor botonGoogle() {
        Image googleIcon = new Image("images/google-icon.svg", "Google");
        googleIcon.setHeight("18px");
        googleIcon.setWidth("18px");
        googleIcon.getStyle().set("margin-right", "var(--lumo-space-s)");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Anchor googleLink;

        if (auth instanceof OAuth2AuthenticationToken oauthToken
                && "google".equals(oauthToken.getAuthorizedClientRegistrationId())) {
            googleLink = new Anchor("#", "Cuenta vinculada con Google");
            googleLink.getStyle().set("pointer-events", "none");
            googleLink.getStyle().set("opacity", "0.7");
        } else {
            // Usuario aún no autenticado con Google
            googleLink = new Anchor("/oauth2/authorization/google", "Acceder con Google");
        }
        googleLink.getStyle()
                .set("background-color", "var(--lumo-primary-color)")
                .set("color", "white")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "0.5em 0em");
        googleLink.getElement().insertChild(0, googleIcon.getElement());
        googleLink.getElement().setAttribute("router-ignore", true);
        googleLink.addClassNames(
                "vaadin-button",
                "vaadin-button-theme-large",
                "vaadin-button-theme-tertiary"
        );

        googleLink.getStyle()
                .set("text-decoration", "none")
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        googleLink.setWidthFull();

        return googleLink;
    }

    private Button sigueinteBoton() {
        Button siguiente = new Button("Siguiente", new Icon(VaadinIcon.ARROW_RIGHT));
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
            String contraseniaTexto1 = contrasenia1.getValue();
            String contraseniaTexto2 = contrasenia2.getValue();
            boolean error = false;
            if (correoElectronico.isEmpty()) {
                correoElectronico.setInvalid(true);
                Notification.show("El campo correo electrónico es obligatorio");
               error = true;
            }
            if (contrasenia1.isEmpty()) {
                contrasenia1.setInvalid(true);
                Notification.show(contrasenia1.getErrorMessage());
                error = true;
            }
            if (contrasenia2.isEmpty()) {
                contrasenia2.setInvalid(true);
                Notification.show(contrasenia2.getErrorMessage());
                error = true;
            }
            if (correoElectronico.isInvalid()) {
                correoElectronico.setInvalid(true);
                Notification.show(correoElectronico.getErrorMessage());
                error = true;
            }
            if (contrasenia1.isEmpty() || contrasenia2.isEmpty()) {
                Notification.show("Debes rellenar todos los campos de contraseña");
                contrasenia1.setInvalid(contrasenia1.isEmpty());
                contrasenia2.setInvalid(contrasenia2.isEmpty());
                error = true;
            }
            if (!contraseniaTexto1.equals(contraseniaTexto2)) {
                Notification.show("Las contraseñas introducidas no coinciden");
                error = true;
            }

            // Verificar si el usuario tiene cuenta Google vinculada
            String accessToken = (String) request.getSession().getAttribute("accessToken");
            if (accessToken == null) {
                Notification.show("Debes vincular tu cuenta de Google antes de continuar");
                error = true;
            }

            if(error)
                return;
            // Redirigir a Registro2View
            request.getSession().setAttribute("correo", correoElectronico.getValue());
            request.getSession().setAttribute("contrasenia", contraseniaTexto1);
            getUI().ifPresent(ui -> ui.navigate("registro2"));
        });
        return siguiente;
    }





    private TextField correoCampo(){
        TextField correoElectronico = new TextField("Correo electrónico");
        correoElectronico.getElement().setAttribute("type","email");
        correoElectronico.setPlaceholder("ejemplo@correo.com");
        correoElectronico.setRequired(true);
        correoElectronico.setPattern("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
        correoElectronico.setErrorMessage("La dirección de correo electronico intrudicida no es válida.");
        return correoElectronico;
    }
    private PasswordField contraseniaCampo(String textoCampo){
        PasswordField contrasenia = new PasswordField(textoCampo);
        contrasenia.setRequired(true);
        contrasenia.setErrorMessage("Este campo es obligatorio");
        return contrasenia;
    }
    private VerticalLayout estiloLogin(){
        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.setAlignItems(Alignment.CENTER);
        loginLayout.getStyle().set("background-color", "var(--lumo-base-color)");
        loginLayout.getStyle().set("border-radius", "var(--lumo-border-radius-l)");
        loginLayout.getStyle().set("box-shadow", "var(--lumo-box-shadow-m)");
        loginLayout.getStyle().set("padding", "var(--lumo-space-xl)");
        loginLayout.setMaxWidth("420px"); // Un poco más de ancho para que respire
        loginLayout.setSpacing(false); // Controlamos el espacio manualmente
        loginLayout.setPadding(true);
        return loginLayout;
    }
    private LoginI18n crearLogin() {
        LoginI18n login = LoginI18n.createDefault();
        login.setHeader(new LoginI18n.Header());
        login.getHeader().setTitle(null);
        login.getHeader().setDescription(null);

        LoginI18n.Form loginForm = login.getForm();
        loginForm.setUsername("Correo electrónico");
        loginForm.setPassword("Contraseña");
        loginForm.setSubmit("Acceder");
        loginForm.setForgotPassword("¿Olvidaste tu contraseña?");

        LoginI18n.ErrorMessage loginError = login.getErrorMessage();
        loginError.setTitle("Error de autenticación");
        loginError.setMessage("Usuario o contraseña incorrectos. Por favor, inténtalo de nuevo.");

        login.setForm(loginForm);
        login.setErrorMessage(loginError);
        return login;
    }

}