package com.example.springfirebaselogin.services;

import com.example.springfirebaselogin.filter.SecurityService;
import com.example.springfirebaselogin.model.entity.Accounts;
import com.example.springfirebaselogin.model.entity.Profile;
import com.example.springfirebaselogin.model.firebase.User;
import com.example.springfirebaselogin.model.request.PostUserAddRequest;
import com.example.springfirebaselogin.model.request.PostUserSearchRequest;
import com.example.springfirebaselogin.model.request.PostUserUpdateRequest;
import com.example.springfirebaselogin.model.response.PostUserAddResponse;
import com.example.springfirebaselogin.model.response.PostUserDetailResponse;
import com.example.springfirebaselogin.model.response.PostUserSearchResponse;
import com.example.springfirebaselogin.model.response.PostUserUpdateResponse;
import com.example.springfirebaselogin.repository.AccountsRepository;
import com.example.springfirebaselogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AccountsRepository accountsRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    public PostUserSearchResponse userSearch(PostUserSearchRequest request) {
        List<Accounts> accountsList = accountsRepository.findByUsernameContainingIgnoreCase(request.getUsername());
        return PostUserSearchResponse.builder()
                .users(accountsList).build();
    }

    public PostUserAddResponse userInsert(PostUserAddRequest request) {
        Accounts account = Accounts.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .createdOn(new Date()).build();
        Accounts newAccount = accountsRepository.save(account);
        return PostUserAddResponse.builder().user(newAccount).build();
    }

    public PostUserUpdateResponse userUpdate(PostUserUpdateRequest request) {
        Optional<Accounts> oldAccount = accountsRepository
                .findByUserId(request.getUserId());
        if (oldAccount.isPresent()) {
            Accounts newAccount = oldAccount.get();
            newAccount.setEmail(request.getEmail());
            newAccount.setPassword(request.getPassword());
            newAccount.setUsername(request.getUsername());
            accountsRepository.save(newAccount);
            return PostUserUpdateResponse.builder().success(true).build();
        } else {
            return PostUserUpdateResponse.builder().success(false).build();
        }
    }


    public PostUserDetailResponse getProfile() {
        User user = securityService.getUser();
        Optional<Profile> userOpt = userRepository.findByProfileId(Integer.parseInt(user.getProfileId()));
        if (userOpt.isEmpty()) {
            return PostUserDetailResponse.builder().responseCode("5000").message("Profile not found").build();
        }
        return PostUserDetailResponse.builder()
                .user(userOpt.get())
                .responseCode("200").build();
    }
}
