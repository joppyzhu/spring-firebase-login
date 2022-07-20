package com.example.springfirebaselogin.model.firebase;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PushNotificationResponse {
    private int status;
    private String message;
}
