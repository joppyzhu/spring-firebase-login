package com.example.springfirebaselogin.model.firebase;

import lombok.Data;

@Data
public class FirebaseProperties {

    private int sessionExpiryInDays;
    private boolean enableStrictServerSession;
    private boolean enableCheckSessionRevoked;
    private boolean enableLogoutEverywhere;

}
