package com.tufg.gestor_reuniones.controller;


import com.tufg.gestor_reuniones.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;

@Controller
public class GoogleController {
    @GetMapping("/registro-token")
    public String obtenerTokenGoogle(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient , HttpSession session) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        Instant expiration = authorizedClient.getAccessToken().getExpiresAt();
        String refreshToken = authorizedClient.getRefreshToken() != null
                ? authorizedClient.getRefreshToken().getTokenValue()
                : null;
        session.setAttribute("accessToken", accessToken);
        session.setAttribute("refreshToken", refreshToken);
        session.setAttribute("expiration", expiration);
        return "redirect:/registro";
    }
}
