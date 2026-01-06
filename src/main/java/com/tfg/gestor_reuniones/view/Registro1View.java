package com.tfg.gestor_reuniones.view;

import com.tfg.gestor_reuniones.service.UsuarioService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

@Route("registro")
@PageTitle("Registro")
@AnonymousAllowed
public class Registro1View extends VerticalLayout {
    private PasswordField contrasenia1;
    private PasswordField contrasenia2;
    private TextField correoElectronico;
    private final HttpServletRequest request;
    private UsuarioService usuarioService;


    public Registro1View(HttpServletRequest request, UsuarioService usuarioService) {
        this.request = request;
        contrasenia1 = contraseniaCampo("Contraseña");
        contrasenia2 =  contraseniaCampo("Confirmar Contraseña");
        correoElectronico = correoCampo();
        this.usuarioService = usuarioService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#eef2f6");

        VerticalLayout registro1Layout = new VerticalLayout();
        registro1Layout.getStyle().set("background-color", "white");
        registro1Layout.getStyle().set("border-radius", "8px");
        registro1Layout.getStyle().set("border", "1px solid #dbdbdb");
        registro1Layout.getStyle().set("box-shadow", "5px 5px 15px rgba(0,0,0,0.1)");
        registro1Layout.setWidth("100%");
        registro1Layout.setMaxWidth("420px");
        registro1Layout.setPadding(true);
        registro1Layout.setSpacing(false);
        registro1Layout.setAlignItems(Alignment.CENTER);

        Icon icono = new Icon(VaadinIcon.CALENDAR_CLOCK);
        icono.setSize("50px");
        icono.setColor("#0D47A1");
        icono.getStyle().set("margin-bottom", "15px");
        // Titulo registrar usuario header
        H1 titulo = new H1("Registrar Usuario");
        Paragraph subtitulo = new Paragraph("Cree su cuenta en el Gestor de Reuniones 1/2");
        subtitulo.getStyle().set("color", "#666666");
        subtitulo.getStyle().set("font-size", "14px");
        subtitulo.getStyle().set("margin-top", "0");
        subtitulo.getStyle().set("margin-bottom", "20px");
        // Añadir mensaje inverso de registro
        HorizontalLayout enlaceLayout = new HorizontalLayout();
        enlaceLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        enlaceLayout.setSpacing(true);
        enlaceLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        enlaceLayout.setSpacing(true);
        enlaceLayout.getStyle().set("font-size", "12px");
        enlaceLayout.getStyle().set("margin-top", "20px");
        enlaceLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        enlaceLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        enlaceLayout.setWidthFull();
        Span textoRegistrado = new Span("¿Ya estás registrado?");
        textoRegistrado.getStyle().set("color", "gray");

        RouterLink enlaceLogin = new RouterLink("Inicia sesión", LoginView.class);
        enlaceLayout.add(textoRegistrado, enlaceLogin);
        enlaceLogin.getStyle().set("font-weight", "bold");
        // Añadir todos los elementos principales
        registro1Layout.add(icono,titulo,subtitulo, botonGoogle(), correoElectronico, contrasenia1, contrasenia2  , sigueinteBoton());
        registro1Layout.add(enlaceLayout);
        add(registro1Layout);
    }
    private Button botonGoogle() {
        Button googleButton = new Button("Acceder con Google");
        googleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        googleButton.setWidthFull();

        String accessToken = (String) request.getSession().getAttribute("accessToken");
        Set<String> listadoPermisos = (Set<String>) request.getSession().getAttribute("scopes");
        googleButton.setEnabled(true);
        googleButton.setText("Acceder con Google");

        if(accessToken != null && listadoPermisos.contains("https://www.googleapis.com/auth/calendar.events") && listadoPermisos.contains("https://www.googleapis.com/auth/calendar.readonly")){
                googleButton.setText("Cuenta vinculada");
                googleButton.setEnabled(false);
        }
        if (accessToken != null && !(listadoPermisos.contains("https://www.googleapis.com/auth/calendar.events") && listadoPermisos.contains("https://www.googleapis.com/auth/calendar.readonly"))) {
            Notification.show("Error vuelve a iniciar sesi\u00F3n debe de activar todos los permisos de los calendarios.");
        }
        googleButton.addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/oauth2/authorization/google");
        });
        return googleButton;
    }

    private Button sigueinteBoton() {
        Button siguiente = new Button("Siguiente", new Icon(VaadinIcon.ARROW_RIGHT));
        siguiente.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        siguiente.getStyle().set("margin-top", "20px");
        siguiente.setIconAfterText(true);


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
                //Notification.show("El campo correo electrónico es obligatorio");
               error = true;
            }
            if (contrasenia1.isEmpty()) {
                contrasenia1.setInvalid(true);
                //Notification.show(contrasenia1.getErrorMessage());
                error = true;
            }
            if (contrasenia2.isEmpty()) {
                contrasenia2.setInvalid(true);
                //Notification.show(contrasenia2.getErrorMessage());
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
            Set<String> listadoPermisos = (Set<String>) request.getSession().getAttribute("scopes");

            if (accessToken == null) {
                Notification.show("Para poder utilizar el gestor de reuniones tiene que vincular su cuenta de Google.");
                error = true;
            }
            if(accessToken != null && !(listadoPermisos.contains("https://www.googleapis.com/auth/calendar.events")
                    && listadoPermisos.contains("https://www.googleapis.com/auth/calendar.readonly"))){
                Notification.show("Error vuelve a iniciar sesi\u00F3n debe de activar todos los permisos de los calendarios.");
                error = true;
            }
            if (usuarioService.findByCorreo(correoElectronico.getValue()) != null) {
                Notification.show("El correo electronico introducido ya ha sido registrado introduzca otro correo distinto.");
                return;
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
        correoElectronico.setWidthFull();
        return correoElectronico;
    }
    private PasswordField contraseniaCampo(String textoCampo){
        PasswordField contrasenia = new PasswordField(textoCampo);
        contrasenia.setRequired(true);
        contrasenia.setErrorMessage("Este campo es obligatorio");
        contrasenia.setWidthFull();
        return contrasenia;
    }


}