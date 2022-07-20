package com.example.springfirebaselogin.filter;

import javax.servlet.http.HttpServletRequest;

import com.example.springfirebaselogin.model.firebase.Credentials;
import com.example.springfirebaselogin.model.firebase.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class SecurityService {

    public User getUser() {
        User userPrincipal = null;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Object principal = securityContext.getAuthentication().getPrincipal();
        if (principal instanceof User) {
            userPrincipal = ((User) principal);
        }
        return userPrincipal;
    }

    public Credentials getCredentials() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return (Credentials) securityContext.getAuthentication().getCredentials();
    }

    public String getBearerToken(HttpServletRequest request) {
        String bearerToken = null;
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            bearerToken = authorization.substring(7, authorization.length());
        }
        return bearerToken;
    }

    public String getDeviceId(HttpServletRequest request) {
        String deviceId = request.getHeader("device-id");
        return deviceId;
    }

    public String getLoginType(HttpServletRequest request) {
        String loginType = request.getHeader("login-type");
        return loginType;
    }

}
