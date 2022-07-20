package com.example.springfirebaselogin.controllers;

import com.example.springfirebaselogin.model.request.PostAuthInitRequest;
import com.example.springfirebaselogin.model.request.PostAuthRegisterRequest;
import com.example.springfirebaselogin.model.response.EmptyResponse;
import com.example.springfirebaselogin.model.response.PostAuthInitResponse;
import com.example.springfirebaselogin.model.response.PostAuthLoginResponse;
import com.example.springfirebaselogin.model.response.PostAuthRegisterResponse;
import com.example.springfirebaselogin.services.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/auth/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("init")
    public PostAuthInitResponse init(PostAuthInitRequest request) {
        return authService.init(request);
    }

    @PostMapping("register")
    public PostAuthRegisterResponse register(PostAuthRegisterRequest request) throws FirebaseAuthException {
        return authService.register(request);
    }

    @PostMapping("login")
    public PostAuthLoginResponse login(HttpServletRequest request) {
        return authService.login(request);
       /*
        EmptyResponse emptyResponse = EmptyResponse.builder().build();
        String idToken = securityService.getBearerToken(request);
        FirebaseUser firebaseUser = securityService.getUser();
        int sessionExpiryDays = secProps.getFirebaseProps().getSessionExpiryInDays();
        long expiresIn = TimeUnit.DAYS.toMillis(sessionExpiryDays);
        SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(expiresIn).build();
        try {
            String sessionCookieValue = FirebaseAuth.getInstance().createSessionCookie(idToken, options);
            cookieUtils.setSecureCookie("session", sessionCookieValue, sessionExpiryDays);
            //cookieUtils.setCookie("authenticated", Boolean.toString(true), sessionExpiryDays);
            //cookieUtils.setCookie("fullname", user.getName().replaceAll("\\s+", "_").toLowerCase(), sessionExpiryDays);
            //cookieUtils.setCookie("pic", user.getPicture(), sessionExpiryDays);
            emptyResponse.setMessage("success");
            emptyResponse.setResponseCode("200");
            return emptyResponse;
        } catch (Exception e) {
            e.printStackTrace();
            emptyResponse.setMessage("error");
            emptyResponse.setResponseCode("5000");
            return emptyResponse;
        }*/
    }

    @PostMapping("logout")
    public EmptyResponse logout() {
        return authService.logout();
    }



}
