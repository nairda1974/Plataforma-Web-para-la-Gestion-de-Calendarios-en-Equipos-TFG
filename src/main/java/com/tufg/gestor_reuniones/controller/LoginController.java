package com.tufg.gestor_reuniones.controller;
import com.tufg.gestor_reuniones.dto.RegistroForm;
import com.tufg.gestor_reuniones.service.GoogleApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private GoogleApiService googleApiService;
    @GetMapping("/")
    public String home() {
        return "login"; // templates/index.html
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            model.addAttribute("name", principal.getAttribute("name"));
            model.addAttribute("email", principal.getAttribute("email"));
        }
        return "dashboard"; // templates/dashboard.html
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String formularioRegistro(Model model, HttpSession session) {
        RegistroForm registroForm = (RegistroForm) session.getAttribute("registroForm");
        if (registroForm == null) {
            registroForm = new RegistroForm();
        }
        model.addAttribute("registroForm", registroForm);

        List<String> zonasHorarias = ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .toList();
        model.addAttribute("zonasHorarias", zonasHorarias);

        return "registro";
    }


    @GetMapping("/registro2")
    public String formularioRegistro2(Model model) {
        RegistroForm registroForm = new RegistroForm();
        model.addAttribute("registroForm", registroForm);
        List<String> zonasHorarias = ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .toList();
        model.addAttribute("zonasHorarias", zonasHorarias);
        return "registro2";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("registroForm") RegistroForm registroForm,
                                   BindingResult br,
                                   Model model,
                                   HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        List<String> listaCalendario = googleApiService.listadoGoogleCalendar(accessToken);
        if (!registroForm.getContrasenia().equals(registroForm.getContrasenia2())) {
            br.reject("contrasenia.noCoincide");
        }
        if (accessToken == null) {
            br.reject("errorGoogleObligatorio");
        }

        if (br.hasErrors()) {
            model.addAttribute("zonasHorarias",
                    ZoneId.getAvailableZoneIds().stream().sorted().toList());
            session.setAttribute("listaCalendario",listaCalendario);
            return "registro";
        }
        session.setAttribute("registroForm", registroForm);
        return "registro2";
    }



}