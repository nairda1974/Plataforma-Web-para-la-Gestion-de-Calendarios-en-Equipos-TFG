package com.tfg.gestor_reuniones.view;

import com.tfg.gestor_reuniones.service.UsuarioService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
@PageTitle("Iniciar Sesión")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();
    @Autowired
    private final UsuarioService usuarioService;

    public LoginView (UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        VerticalLayout loginLayout = new VerticalLayout();
        // estilo en loginLayout
        loginLayout.getStyle().set("background-color", "white");
        loginLayout.getStyle().set("border-radius", "8px");
        loginLayout.getStyle().set("border", "1px solid #dbdbdb");
        loginLayout.getStyle().set("box-shadow", "5px 5px 15px rgba(0,0,0,0.1)");
        loginLayout.setWidth("100%");
        loginLayout.setMaxWidth("450px");
        loginLayout.setAlignItems(Alignment.CENTER);
        getStyle().set("background-color", "#eef2f6");

        Icon icon = new Icon(VaadinIcon.CALENDAR_CLOCK);
        icon.setSize("50px");
        icon.setColor("#0D47A1");
        icon.getStyle().set("margin-bottom", "15px");
        loginLayout.setPadding(true);
        loginLayout.setSpacing(false);
        H1 titulo = new H1("Iniciar Sesión");
        Paragraph subtitulo = new Paragraph("Bienvenido al Gestor de Reuniones");
        subtitulo.getStyle().set("color", "#666666");
        subtitulo.getStyle().set("font-size", "14px");
        subtitulo.getStyle().set("margin-top", "0");
        subtitulo.getStyle().set("margin-bottom", "20px");

        loginForm.setAction("login");
        loginForm.setI18n(crearLogin());
        // Solucion de github vaadin para evitar saltar error al ir al registro fix: Prevent Login examples from stealing focus
        loginForm.getElement().setAttribute("no-autofocus", "");
        loginForm.setForgotPasswordButtonVisible(false); // seteo a falso para eliminar olvide mi contrasenia
        loginLayout.add(icon, titulo, subtitulo, loginForm);
        HorizontalLayout enlaceLayout = new HorizontalLayout();
        enlaceLayout.setSpacing(true);

        enlaceLayout.getStyle().set("font-size", "12px");
        enlaceLayout.getStyle().set("margin-top", "20px");

        enlaceLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        enlaceLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        enlaceLayout.setWidthFull();
        Span textoRegistro = new Span("No tienes cuenta?");
        textoRegistro.getStyle().set("color", "gray");
        RouterLink enlace = new RouterLink("Regístrate", Registro1View.class);
        enlaceLayout.add(textoRegistro, enlace);

        loginLayout.add(enlaceLayout);

        add(loginLayout);
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
        loginForm.setTitle(null); // Por defecto vaadin pone un texto generico Log in para dejar el mio de Iniciar sesion

        LoginI18n.ErrorMessage loginError = login.getErrorMessage();
        loginError.setTitle("Error de autenticación");
        loginError.setMessage("Usuario o contraseña incorrectos. Por favor, inténtalo de nuevo.");

        login.setForm(loginForm);
        login.setErrorMessage(loginError);
        return login;
    }

//https://vaadin.com/docs/latest/building-apps/security/add-login
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }
}