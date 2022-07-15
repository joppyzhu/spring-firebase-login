package com.example.springfirebaselogin.model.response;

import com.example.springfirebaselogin.model.entity.Accounts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUserAddResponse {
    private Accounts user;
}
