package com.example.springfirebaselogin.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUserAddRequest {
    private String username;
    private String password;
    private String email;
}
