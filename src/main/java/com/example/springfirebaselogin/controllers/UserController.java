package com.example.springfirebaselogin.controllers;

import com.example.springfirebaselogin.model.request.EmptyRequest;
import com.example.springfirebaselogin.model.request.PostUserAddRequest;
import com.example.springfirebaselogin.model.request.PostUserSearchRequest;
import com.example.springfirebaselogin.model.request.PostUserUpdateRequest;
import com.example.springfirebaselogin.model.response.EmptyResponse;
import com.example.springfirebaselogin.model.response.PostUserAddResponse;
import com.example.springfirebaselogin.model.response.PostUserDetailResponse;
import com.example.springfirebaselogin.model.response.PostUserSearchResponse;
import com.example.springfirebaselogin.model.response.PostUserUpdateResponse;
import com.example.springfirebaselogin.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("search")
    public PostUserSearchResponse userSearch(@RequestBody PostUserSearchRequest request) {
        return userService.userSearch(request);
    }

    @PostMapping("detail")
    public PostUserDetailResponse getProfileDetail() {
        return userService.getProfile();
    }

    @PostMapping("insert")
    public PostUserAddResponse userInsert(@RequestBody PostUserAddRequest request) {
        return userService.userInsert(request);
    }

    @PostMapping("update")
    public PostUserUpdateResponse userUpdate(@RequestBody PostUserUpdateRequest request) {
        return userService.userUpdate(request);
    }

    @PostMapping("notif")
    public EmptyResponse sendNotif(PostUserUpdateRequest request) {
        return userService.sendNotif(request);
    }

}
