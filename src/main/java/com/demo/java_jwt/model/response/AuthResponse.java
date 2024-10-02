package com.demo.java_jwt.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class AuthResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 8286210631647330695L;

    private String user;
    private String token;
    private String refreshToken;

    public AuthResponse(String user, String token, String refreshToken) {
        this.user = user;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
