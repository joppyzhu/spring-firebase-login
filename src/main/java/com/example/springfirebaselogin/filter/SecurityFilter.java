package com.example.springfirebaselogin.filter;

import com.example.springfirebaselogin.model.firebase.Credentials;
import com.example.springfirebaselogin.model.firebase.User;
import com.example.springfirebaselogin.model.firebase.SecurityProperties;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SecurityProperties securityProps;

    @Autowired
    private CookieService cookieUtils;

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        authorize(request);
        filterChain.doFilter(request, response);
    }

    private void authorize(HttpServletRequest request) {
        String sessionCookieValue = null;
        FirebaseToken decodedToken = null;
        Credentials.CredentialType type = null;
        // Token verification
        boolean strictServerSessionEnabled = securityProps.getFirebaseProps().isEnableStrictServerSession();
        Cookie sessionCookie = cookieUtils.getCookie("session");
        String token = securityService.getBearerToken(request);
        try {
            if (sessionCookie != null) {
                sessionCookieValue = sessionCookie.getValue();
                decodedToken = firebaseAuth.verifySessionCookie(sessionCookieValue,
                        securityProps.getFirebaseProps().isEnableCheckSessionRevoked());
                type = Credentials.CredentialType.SESSION;
            } else if (!strictServerSessionEnabled && token != null && !token.equals("null")
                    && !token.equalsIgnoreCase("undefined")) {
                decodedToken = firebaseAuth.verifyIdToken(token);
                type = Credentials.CredentialType.ID_TOKEN;
            }
        } catch (FirebaseAuthException e) {
            log.error("Firebase Exception:: ", e.getLocalizedMessage());
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        User user = firebaseTokenToUserDto(decodedToken);
        // Handle roles
        if (user != null) {
            // Handle roles
            //decodedToken.getClaims().forEach((k, v) -> authorities.add(new SimpleGrantedAuthority("USER")));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getScope()));
            // Set security context
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
                    new Credentials(type, decodedToken, token, sessionCookieValue), authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private User firebaseTokenToUserDto(FirebaseToken decodedToken) {
        User user = null;
        if (decodedToken != null) {
            user = new User();
            user.setUid(decodedToken.getUid());
            user.setName(decodedToken.getName());
            user.setEmail(decodedToken.getEmail());
            user.setPicture(decodedToken.getPicture());
            user.setIssuer(decodedToken.getIssuer());
            user.setEmailVerified(decodedToken.isEmailVerified());
            Map<String, Object> claims = decodedToken.getClaims();
            if (Objects.nonNull(claims.get("device_id"))) {
                user.setDeviceId((String) claims.get("device_id"));
            } else {
                user.setDeviceId("0");
            }
            if (Objects.nonNull(claims.get("profile_id"))) {
                user.setProfileId((String) claims.get("profile_id"));
            } else {
                user.setProfileId("0");
            }
            if (Objects.nonNull(claims.get("scope"))) {
                user.setScope((String) claims.get("scope"));
            } else {
                user.setScope(Scope.ANONYMOUS.getValue());
            }

        }
        return user;
    }

}
