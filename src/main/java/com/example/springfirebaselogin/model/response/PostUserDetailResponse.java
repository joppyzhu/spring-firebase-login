package com.example.springfirebaselogin.model.response;

import com.example.springfirebaselogin.model.entity.Accounts;
import com.example.springfirebaselogin.model.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class PostUserDetailResponse extends BaseResponse {
    private Profile user;
}
