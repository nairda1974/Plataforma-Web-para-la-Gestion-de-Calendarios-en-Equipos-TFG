package com.tfg.gestor_reuniones.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class AuteticacionGoogleController {
    // Basado en https://docs.spring.io/spring-security/reference/servlet/oauth2/client/authorized-clients.html
    // https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/oauth2/core/OAuth2AccessToken.html
    @GetMapping("/registro-token")
    public String obtenerTokenGoogle(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, HttpSession session) {
        OAuth2RefreshToken refreshTokenOauth = authorizedClient.getRefreshToken();
        OAuth2AccessToken accessTokenTokenOauth = authorizedClient.getAccessToken();
        Set<String> listadoScope = Set.of();
        String refreshToken = null;
        String accessToken = null;
        if (refreshTokenOauth != null)
            refreshToken = refreshTokenOauth.getTokenValue();
        if(accessTokenTokenOauth != null) {
            listadoScope = accessTokenTokenOauth.getScopes();
            accessToken = accessTokenTokenOauth.getTokenValue();
        }
        session.setAttribute("refreshToken", refreshToken);
        session.setAttribute("scopes", listadoScope);
        session.setAttribute("accessToken", accessToken) ;
        return "redirect:/registro";
    }
}

