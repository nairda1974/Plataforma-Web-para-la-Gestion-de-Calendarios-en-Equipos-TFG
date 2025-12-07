package com.tufg.gestor_reuniones.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Route("login")
@PageTitle("Iniciar Sesión")
@AnonymousAllowed
@CssImport("./styles/login-view.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();
    private final AuthenticationContext authenticationContext;
    public LoginView(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        getStyle().set("background", "linear-gradient(45deg, #A7B6E9 0%, #E9EBF9 100%)");

        VerticalLayout loginLayout =  estiloLogin();

        Icon icon = new Icon(VaadinIcon.CALENDAR_CLOCK);
        icon.setSize("50px");
        icon.setColor("var(--lumo-primary-color)");
        icon.getStyle().set("margin-bottom", "var(--lumo-space-s)");

        H1 title = new H1("Iniciar Sesión");
        title.addClassName("login-title");

        Paragraph subtitle = new Paragraph("Bienvenido al Gestor de Reuniones");
        subtitle.getStyle().set("color", "var(--lumo-contrast-70pct)");
        subtitle.getStyle().set("font-size", "var(--lumo-font-size-m)");
        subtitle.getStyle().set("margin-top", "0");
        subtitle.getStyle().set("margin-bottom", "var(--lumo-space-l)");

        loginForm.setAction("login");
        loginForm.setI18n(crearLogin());

        // Ocultamos el botón de "olvidé mi contraseña"
        loginForm.setForgotPasswordButtonVisible(false);

        loginForm.getElement()
                .executeJs("this.$.username.pattern = '^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$';");
        loginForm.getElement()
                .executeJs("this.$.username.title = 'Introduce un correo electrónico válido';");
        loginForm.getElement()
                .executeJs("this.$.username.setAttribute('type', 'email');");

        loginForm.getElement().executeJs("this.shadowRoot.querySelector('[part=\"header\"]')?.remove();");

        loginForm.getStyle().set("padding", "0");
        loginForm.getStyle().set("box-shadow", "none");


        loginLayout.add(icon, title, subtitle, loginForm);

        // --- INICIO DE LA MODIFICACIÓN ---

        // 1. Creamos un layout horizontal para alinear el texto y el enlace
        HorizontalLayout registerLayout = new HorizontalLayout();
        registerLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE); // Alinear texto
        registerLayout.setSpacing(true); // Espacio entre "texto?" y "enlace"
        registerLayout.getStyle()
                .set("font-size", "var(--lumo-font-size-s)") // Tamaño de fuente pequeño
                .set("margin-top", "var(--lumo-space-m)"); // Espacio sobre el enlace

        // 2. Creamos el texto
        Span registerText = new Span("No tienes cuenta?");
        registerText.getStyle().set("color", "var(--lumo-contrast-70pct)"); // Color de texto secundario

        // 3. Creamos el enlace con la URL "registro1"
        Anchor registerLink = new Anchor("registro", "Regístrate");

        // --- FIN DE LA MODIFICACIÓN ---

        // 4. Añadimos ambos al layout horizontal
        registerLayout.add(registerText, registerLink);

        // 5. Añadimos el layout horizontal al layout principal
        loginLayout.add(registerLayout);

        add(loginLayout);
    }
    private VerticalLayout estiloLogin(){
        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.setAlignItems(Alignment.CENTER);
        loginLayout.getStyle().set("background-color", "var(--lumo-base-color)");
        loginLayout.getStyle().set("border-radius", "var(--lumo-border-radius-l)");
        loginLayout.getStyle().set("box-shadow", "var(--lumo-box-shadow-m)");
        loginLayout.getStyle().set("padding", "var(--lumo-space-xl)");
        loginLayout.setMaxWidth("420px");
        loginLayout.setSpacing(false);
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

        LoginI18n.ErrorMessage loginError = login.getErrorMessage();
        loginError.setTitle("Error de autenticación");
        loginError.setMessage("Usuario o contraseña incorrectos. Por favor, inténtalo de nuevo.");

        login.setForm(loginForm);
        login.setErrorMessage(loginError);
        return login;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
        if (authenticationContext.isAuthenticated()) {
            event.forwardTo(HomeView.class);
        }
    }
}