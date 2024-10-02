package com.demo.java_jwt.model.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserSignupRequestVO {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNo;
    private List<RoleDto> roleDtoList;
}
