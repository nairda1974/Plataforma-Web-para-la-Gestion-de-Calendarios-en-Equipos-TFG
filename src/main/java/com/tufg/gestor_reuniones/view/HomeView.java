package com.tufg.gestor_reuniones.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route(value = "home")
@PageTitle("home")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    public HomeView() {
        // Header
        H2 cabecera = new H2("Cabecera");

        Button print = new Button(new Icon(VaadinIcon.PRINT));
        Button external = new Button(new Icon(VaadinIcon.EXTERNAL_LINK));
        external.addClickListener(e ->
                UI.getCurrent().getPage().open("https://vaadin.com", "_blank")
        );

        HorizontalLayout header = new HorizontalLayout(cabecera, print, external);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setFlexGrow(1, cabecera);   // ahora sí, con el componente
        header.setPadding(true);
        header.setSpacing(true);
        header.setWidthFull();
        header.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        header.addClassName("box-shadow-s"); // clase CSS propia

        add(header);

        // Content
        Paragraph text = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        VerticalLayout content = new VerticalLayout(text);
        content.setPadding(true);

        add(content);

    }
}