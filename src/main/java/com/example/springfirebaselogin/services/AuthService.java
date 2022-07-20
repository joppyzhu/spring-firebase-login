package com.example.springfirebaselogin.services;

import com.example.springfirebaselogin.filter.CookieService;
import com.example.springfirebaselogin.filter.SecurityService;
import com.example.springfirebaselogin.model.entity.Device;
import com.example.springfirebaselogin.model.entity.Profile;
import com.example.springfirebaselogin.model.entity.UserDevice;
import com.example.springfirebaselogin.model.firebase.Credentials;
import com.example.springfirebaselogin.model.firebase.SecurityProperties;
import com.example.springfirebaselogin.model.firebase.User;
import com.example.springfirebaselogin.model.request.PostAuthInitRequest;
import com.example.springfirebaselogin.model.request.PostAuthRegisterRequest;
import com.example.springfirebaselogin.model.response.EmptyResponse;
import com.example.springfirebaselogin.model.response.PostAuthInitResponse;
import com.example.springfirebaselogin.model.response.PostAuthLoginResponse;
import com.example.springfirebaselogin.model.response.PostAuthRegisterResponse;
import com.example.springfirebaselogin.repository.DeviceRepository;
import com.example.springfirebaselogin.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SessionCookieOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SecurityService securityService;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final SecurityProperties secProps;
    private final CookieService cookieUtils;

    public PostAuthInitResponse init(PostAuthInitRequest request) {
        String key = "";
        String deviceId = "";
        if (request != null && request.getDeviceId() != null) {
            Optional<Device> deviceOpt = deviceRepository.findByDeviceName(request.getDeviceId());
            if (!deviceOpt.isEmpty()) {
                Device device = deviceOpt.get();
                deviceId = device.getDeviceName();
                key = device.getKey();
                if (request.getToken() != null
                        && !request.getToken().isEmpty()
                        && !request.getToken().equalsIgnoreCase(device.getToken())) {
                    device.setToken(request.getToken());
                    deviceRepository.save(device);
                }
            }
        }
        if (deviceId.isEmpty()) {
            deviceId = UUID.randomUUID().toString();
            key = UUID.randomUUID().toString().replace("-","");
            Device device = Device.builder()
                    .deviceName(deviceId)
                    .token(request.getToken())
                    .key(key).build();
            deviceRepository.save(device);
        }
        return PostAuthInitResponse.builder()
                .deviceId(deviceId)
                .key(key).build();
    }

    public PostAuthRegisterResponse register(PostAuthRegisterRequest request) {
        User user = securityService.getUser();
        Optional<Profile> userOpt = userRepository.findByEmail(user.getEmail());
        if (userOpt.isEmpty()) {
            return PostAuthRegisterResponse.builder().responseCode("5000").message("Profile not found").build();
        }
        Optional<Profile> usernameOpt = userRepository.findByUsername(request.getUsername());
        if (!usernameOpt.isEmpty()) {
            return PostAuthRegisterResponse.builder().responseCode("5000").message("Username already used").build();
        }
        Profile profile = userOpt.get();
        profile.setDisplayName(request.getDisplayName());
        profile.setUsername(request.getUsername());
        profile.setPhone(request.getPhone());
        userRepository.save(profile);
        return PostAuthRegisterResponse.builder().responseCode("200").message("Success").build();
    }

    public PostAuthLoginResponse login(HttpServletRequest request) {
        User user = securityService.getUser();
        // Firebase Stuff
        String idToken = securityService.getBearerToken(request);
        int sessionExpiryDays = secProps.getFirebaseProps().getSessionExpiryInDays();
        long expiresIn = TimeUnit.DAYS.toMillis(sessionExpiryDays);
        SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(expiresIn).build();
        try {
            String sessionCookieValue = FirebaseAuth.getInstance().createSessionCookie(idToken, options);
            cookieUtils.setSecureCookie("session", sessionCookieValue, sessionExpiryDays);
        } catch (Exception e) {
            return PostAuthLoginResponse.builder().responseCode("5000").message("Error").build();
        }
        // End of Firebase Stuff
        String deviceId = securityService.getDeviceId(request);
        String loginType = securityService.getLoginType(request);
        Optional<Device> deviceOpt = deviceRepository.findByDeviceName(deviceId);
        if (deviceOpt.isEmpty()) {
            return PostAuthLoginResponse.builder().responseCode("5001").message("Device not found").build();
        }
        Device device = deviceOpt.get();
        if (!user.isEmailVerified()) {
            return PostAuthLoginResponse.builder().responseCode("5002").message("Please verify your email").build();
        }
        Optional<Profile> userOpt = userRepository.findByEmail(user.getEmail());
        Profile profile = null;
        if (userOpt.isEmpty()) {
            Profile newProfile = Profile.builder()
                    .email(user.getEmail())
                    .emailVerified(true)
                    .status("ACTIVE")
                    .scope("USER")
                    .loginType(loginType).build();
            profile = userRepository.save(newProfile);
        } else {
            profile = userOpt.get();
        }
        // Sign this device to profileId
        device.setProfileId(profile.getProfileId());
        device.setIsActive(1);
        deviceRepository.save(device);

        String customToken = generateCustomToken(user.getUid(), device.getDeviceId(), profile.getProfileId(), profile.getScope());
        if (Objects.isNull(profile.getDisplayName()) || profile.getDisplayName().isEmpty()) {
            return PostAuthLoginResponse.builder()
                    .accessToken(customToken)
                    .responseCode("5003")
                    .message("Please complete your profile").build();
        } else {
            return PostAuthLoginResponse.builder()
                    .accessToken(customToken)
                    .responseCode("200")
                    .message("Success").build();
        }
    }

    public EmptyResponse logout() {
        User user = securityService.getUser();
        // Non active deviceId
        deviceRepository.deactivateDevice(Integer.parseInt(user.getDeviceId()), Integer.parseInt(user.getProfileId()));

        if (securityService.getCredentials().getType() == Credentials.CredentialType.SESSION
                && secProps.getFirebaseProps().isEnableLogoutEverywhere()) {
            try {
                FirebaseAuth.getInstance().revokeRefreshTokens(securityService.getUser().getUid());
            } catch (FirebaseAuthException e) {
                e.printStackTrace();
            }
        }
        cookieUtils.deleteSecureCookie("session");
        return EmptyResponse.builder().responseCode("200").message("success").build();
    }

    private String generateCustomToken(String uid, Integer deviceId, Integer profileId, String scope) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("device_id", String.valueOf(deviceId));
            claims.put("profile_id", String.valueOf(profileId));
            claims.put("scope", scope);
            return FirebaseAuth.getInstance().createCustomToken(uid, claims);
        } catch (Exception e) {
            return "";
        }
    }
}
