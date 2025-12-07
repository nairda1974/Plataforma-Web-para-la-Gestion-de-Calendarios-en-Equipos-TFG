package com.tufg.gestor_reuniones.view;

import com.tufg.gestor_reuniones.model.Disponibilidad;
import com.tufg.gestor_reuniones.model.Evento;
import com.tufg.gestor_reuniones.repository.DisponibilidadRepository;
import com.tufg.gestor_reuniones.repository.EventoRepository;
import com.tufg.gestor_reuniones.service.EventoService;
import com.tufg.gestor_reuniones.service.ReservaService;
import com.tufg.gestor_reuniones.service.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class CrearReunionDialog extends Dialog {
    private Map<Integer, TimePicker> horaInicio = new HashMap<>();
    private Map<Integer, TimePicker> horaFin = new HashMap<>();
    private TextField tituloCampo;
    private TextField descripcionCampo;
    private Select<Integer> desplegableDuracion;
    private UsuarioService usuarioService;
    private DisponibilidadRepository disponibilidadRepository;
    private EventoService eventoService;
    private MultiSelectComboBox<String> desplegableUsuario;
    public CrearReunionDialog(EventoRepository eventoRepository, UsuarioService usuarioService , DisponibilidadRepository disponibilidadRepository){
        this.usuarioService = usuarioService;
        this.eventoService = new EventoService(eventoRepository, usuarioService , disponibilidadRepository);
        setSizeFull(); // Aumenta el tamanio del dialogo
        H2 tituloHeader = new H2("Crear una nueva reunión");
        tituloHeader.getStyle().set("margin", "0 auto");
        getHeader().add(tituloHeader);
        VerticalLayout dialogLayout = new VerticalLayout();
        this.tituloCampo = textoEntrada("Título");
        this.descripcionCampo = textoEntrada("Descripción");
        this.desplegableDuracion = desplegableDuracion();
        this.desplegableUsuario = desplegableMultipleUsuario();
        dialogLayout.add(tituloCampo,descripcionCampo, desplegableDuracion, desplegableUsuario);

        add(dialogLayout, crearHorario());

        Button cancelar = new Button("Cancel", (e) -> close());
        cancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelar.getStyle().setColor("red");
        getFooter().add(cancelar);

        Button confirmar = new Button("Confirmar reunión", (e) -> {
            confirmarEvento();
        });
        confirmar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getFooter().add(confirmar);
    }
    private TextField textoEntrada(String campo) {
        TextField textField = new TextField();
        textField.setLabel(campo);
        textField.setRequiredIndicatorVisible(true);
        textField.setSizeFull();
        return textField;
    }
    private Select<Integer> desplegableDuracion(){
        Select<Integer> desplegable = new Select<>();
        desplegable.setLabel("Duración");
        desplegable.setItems(15, 30,
                45, 60,  120);
        desplegable.setValue(30);
        desplegable.setSizeFull();
        desplegable.setItemLabelGenerator(item -> item + "minutos");
        return desplegable;
    }
    private MultiSelectComboBox<String> desplegableMultipleUsuario(){
        MultiSelectComboBox<String> desplegableUsuarios = new MultiSelectComboBox<>(
                "Participantes");

        desplegableUsuarios.setItems(
                query -> {
                    var listaCorreos = usuarioService.obtenerCorreos(query.getFilter().orElse(""));
                    return listaCorreos.stream()
                            .skip(query.getOffset())
                            .limit(query.getLimit());
                });
        desplegableUsuarios.setSizeFull();
        return desplegableUsuarios;
    }
    private List<Disponibilidad> setListadoDisponibilidad(){
        List<Disponibilidad> disponibilidadLista = new ArrayList<>();
        List<Integer> diasSemana = List.of(1, 2, 3, 4, 5, 6, 7);
        for(Integer dia: diasSemana){
            Disponibilidad disponibilidad = new Disponibilidad();
            LocalTime inicio = horaInicio.get(dia).getValue();
            LocalTime fin = horaFin.get(dia).getValue();

            if (inicio == null || fin == null) {
                continue;
            }

            disponibilidad.setDiaSemana(dia);
            disponibilidad.setHoraInicio(inicio);
            disponibilidad.setHoraFin(fin);
            disponibilidadLista.add(disponibilidad);
        }
        return disponibilidadLista;
    }
    private VerticalLayout crearHorario() {
        List<String> diasSemana = List.of("Lunes", "Martes", "Miércoles",
                "Jueves", "Viernes", "Sábado", "Domingo");

        VerticalLayout horarioLayout = new VerticalLayout();
        horarioLayout.setSpacing(true);
        horarioLayout.setPadding(false);
        horarioLayout.setWidthFull();

        H3 header = new H3("Define tu disponibilidad horaria");
        header.getStyle().set("margin-bottom", "1rem");
        horarioLayout.add(header);

        for (int i=0 ;i<7; i++) {
            String dia = diasSemana.get(i);
            HorizontalLayout diaLayout = new HorizontalLayout();
            diaLayout.setWidthFull();
            diaLayout.setSpacing(true);
            diaLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            diaLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            Span diaSpan = new Span(dia + ":");
            diaSpan.setWidth("100px");
            diaSpan.getStyle()
                    .set("font-weight", "500")
                    .set("text-align", "right")
                    .set("padding-right", "0.5rem");

            TimePicker inicio = new TimePicker();
            inicio.setPlaceholder("Inicio");
            inicio.setStep(Duration.ofMinutes(30));
            inicio.setWidth("150px");
            inicio.setLocale(new Locale("es", "ES"));

            Span guion = new Span("—");
            guion.getStyle()
                    .set("margin", "0 0.2rem")
                    .set("color", "var(--lumo-secondary-text-color)");

            TimePicker fin = new TimePicker();
            fin.setPlaceholder("Fin");
            fin.setStep(Duration.ofMinutes(30));
            fin.setWidth("150px");
            fin.setLocale(new Locale("es", "ES"));

            diaLayout.add(diaSpan, inicio, guion, fin);
            horarioLayout.add(diaLayout);
            horaInicio.put(i + 1, inicio);
            horaFin.put(i + 1, fin);
        }

        return horarioLayout;
    }
    private void confirmarEvento(){
        boolean error = false;
        if(this.tituloCampo.isEmpty()){
            error = true;
            Notification.show("Es obligatorio el t\u00EDtulo.");
        }
        if(this.descripcionCampo == null){
            error = true;
            Notification.show("Es obligatorio la descripción de la reuni\u00F3n.");
        }
        if(this.desplegableUsuario.isEmpty()){
            error = true;
            Notification.show("Has de seleccionar al menos un participante en la reuni\u00F3n.");
        }
        if(this.desplegableDuracion.isEmpty()){
            error = true;
            Notification.show("Has de seleccionar la duraci\u00F3n de la reuni\u00F3n.");
        }

        List<Disponibilidad> listaDisponibilidad = new ArrayList<>();
        List<String> semana = List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        boolean existeUnDia = false;
        for(int i = 1; i<8 ; i++){
            Disponibilidad disponibilidad = new Disponibilidad();
            if(this.horaInicio == null && this.horaFin == null){
                continue;
            }
            if(this.horaInicio == null || this.horaFin == null){
                error = true;
                Notification.show(semana.get(i-1)+": Has de seleccionar una hora m\u00EDnima y una hora m\u00E1xima.");
            }
            if (this.horaInicio.get(i).getValue() != null &&
                    this.horaFin.get(i).getValue() != null &&
                    this.horaInicio.get(i).getValue().isAfter(this.horaFin.get(i).getValue())) {
                error = true;
                Notification.show("La hora m\u00E1xima ha de ser superior a la hora m\u00EDnima.");
            }
            if(!error && horaInicio.get(i).getValue() != null && horaFin.get(i) != null){
                disponibilidad.setHoraInicio(horaInicio.get(i).getValue());
                disponibilidad.setHoraFin(horaFin.get(i).getValue());
                disponibilidad.setDiaSemana(i);
                existeUnDia = true;
                listaDisponibilidad.add(disponibilidad);
            }

        }
        if(!existeUnDia){
            error = true;
            Notification.show("Has de seleccionar la disponibilidad de al menos un dia.");
        }
        if(error)
            return;
        eventoService.crearEvento(this.tituloCampo.getValue(),this.descripcionCampo.getValue(),this.desplegableDuracion.getValue(),this.desplegableUsuario.getValue(),listaDisponibilidad);
        close();
    }
}
