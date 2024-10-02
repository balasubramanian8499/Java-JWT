package com.demo.java_jwt.service;

import com.demo.java_jwt.model.response.LoginRequest;
import com.demo.java_jwt.model.response.LoginResponse;
import com.demo.java_jwt.model.response.SuccessResponse;
import com.demo.java_jwt.model.response.UserSignupRequestVO;

public interface AuthService {

    LoginResponse login(LoginRequest response);

    LoginResponse refreshToken(String refreshToken);

    SuccessResponse<Object> signup(UserSignupRequestVO request);

}
