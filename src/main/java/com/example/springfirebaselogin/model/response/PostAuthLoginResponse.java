package com.example.springfirebaselogin.model.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class PostAuthLoginResponse extends BaseResponse {
    private String accessToken;
}
