package com.demo.java_jwt.model.response;

import com.demo.java_jwt.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private UserDto user;

    private String token;

    private String refreshToken;

    public LoginResponse(UserDto user, String token, String refreshToken) {
        this.user = user;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
