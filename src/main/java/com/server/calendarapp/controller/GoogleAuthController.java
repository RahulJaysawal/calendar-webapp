package com.server.calendarapp.controller;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.server.calendarapp.security.CurrentUser;
import com.server.calendarapp.security.CustomerPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    @Qualifier("myFlow")
    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    @Qualifier("myAuthCodeRequestUrl")
    private AuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl;

    @Value("${google.redirectURI}")
    private String redirectURI;

    @Autowired
    public GoogleAuthController(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow
            , AuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
        this.googleAuthorizationCodeRequestUrl = googleAuthorizationCodeRequestUrl;
    }

    @GetMapping("/login")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleAuthorizationCodeRequestUrl.build());
    }


    @PostMapping("/login/callback")
    public void googleLoginCallback(@RequestParam(value = "code") String code, @CurrentUser CustomerPrinciple currentUser) {

        try {
            TokenResponse response = googleAuthorizationCodeFlow
                    .newTokenRequest(code)
                    .setRedirectUri(redirectURI)
                    .execute();
            googleAuthorizationCodeFlow.createAndStoreCredential(response, currentUser.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }


}
