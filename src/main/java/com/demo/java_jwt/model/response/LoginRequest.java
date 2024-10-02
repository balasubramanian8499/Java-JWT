package com.demo.java_jwt.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String userName;
    private String password;
}