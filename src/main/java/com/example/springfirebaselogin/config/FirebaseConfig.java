package com.example.springfirebaselogin.config;

import com.example.springfirebaselogin.model.firebase.FirebaseParameter;
import com.example.springfirebaselogin.model.firebase.SecurityProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.type}")
    private String type;

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.private-key-id}")
    private String privateKeyId;

    @Value("${firebase.private-key}")
    private String privateKey;

    @Value("${firebase.client-email}")
    private String clientEmail;

    @Value("${firebase.client-id}")
    private String clientId;

    @Value("${firebase.auth-uri}")
    private String authUri;

    @Value("${firebase.token-uri}")
    private String tokenUri;

    @Value("${firebase.auth-provider-x509-cert-url}")
    private String authProviderC509CertUrl;

    @Value("${firebase.client-x509-cert-url}")
    private String clientX509CertUrl;

    @Primary
    @Bean
    public FirebaseApp getfirebaseApp() throws IOException {
        FirebaseParameter firebaseParameter = FirebaseParameter.builder()
                .type(type)
                .project_id(projectId)
                .private_key_id(privateKeyId)
                .private_key(privateKey.replace("\\n", "\n"))
                .client_email(clientEmail)
                .client_id(clientId)
                .auth_uri(authUri)
                .token_uri(tokenUri)
                .auth_provider_x509_cert_url(authProviderC509CertUrl)
                .client_x509_cert_url(clientX509CertUrl).build();
        String firebaseParameterString = new ObjectMapper().writeValueAsString(firebaseParameter);
        InputStream targetStream = new ByteArrayInputStream(firebaseParameterString.getBytes());
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(targetStream)).build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseAuth getAuth() throws IOException {
        return FirebaseAuth.getInstance(getfirebaseApp());
    }

    @Bean
    public FirebaseMessaging getMessaging() throws IOException {
        return FirebaseMessaging.getInstance(getfirebaseApp());
    }
}
