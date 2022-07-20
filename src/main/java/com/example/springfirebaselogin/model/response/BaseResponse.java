package com.example.springfirebaselogin.model.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
public class BaseResponse implements Serializable {
    private String responseCode;
    private String message;
}
