package com.demo.java_jwt.model.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private String userName;
    private String phoneNumber;
    private boolean isActive;
    private boolean deletedFlag;
    private List<RoleDto> userRoleList;

}
