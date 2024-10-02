package com.demo.java_jwt.service.impl;

import com.demo.java_jwt.exception.ResourceNotFoundException;
import com.demo.java_jwt.model.Role;
import com.demo.java_jwt.model.User;
import com.demo.java_jwt.model.UserRole;
import com.demo.java_jwt.model.response.*;
import com.demo.java_jwt.repo.RoleRepository;
import com.demo.java_jwt.repo.UserRepository;
import com.demo.java_jwt.repo.UserRoleRepository;
import com.demo.java_jwt.service.AuthService;
import com.demo.java_jwt.util.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) throws ResourceNotFoundException {
        User userDetails = getUserInfo(request.getUserName());
        AuthResponse authResponse = jwtUtils.authenticate(userDetails, request.getUserName(), request.getPassword(), false);
        UserDto userDTO = getUserDTOByEmail(request.getUserName());
        return new LoginResponse(userDTO, authResponse.getToken(), authResponse.getRefreshToken());
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        String email = jwtUtils.getUsernameFromToken(refreshToken);
        if (email == null) {
            throw new ResourceNotFoundException("INVALID_INPUT");
        }
        User object = getUserInfo(email);
        AuthResponse authResponse = jwtUtils.authenticate(object, email, null, true);
        UserDto userDTO = getUserDTOByEmail(email);
        return new LoginResponse(userDTO, authResponse.getToken(), authResponse.getRefreshToken());
    }

    @Override
    public SuccessResponse<Object> signup(UserSignupRequestVO request) {
        SuccessResponse<Object> successResponse = new SuccessResponse<>();
        if (Objects.nonNull(request)) {
            Optional<User> existingUser = userRepository.findByUserNameAndIsActiveTrue(request.getEmail());
            if (existingUser.isPresent()) {
                throw new ResourceNotFoundException("EMAIL_ALREADY_EXISTS");
            }
            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNo());
            user.setUserName(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setIsActive(true);
            user.setDeletedFlag(false);
            User saveuser = userRepository.save(user);
            saveRole(request.getRoleDtoList(),saveuser);
        }
        successResponse.setData("User signup successful");
        return successResponse;
    }

    private void saveRole(List<RoleDto> roleDtoList, User user) {
        try {
            List<UserRole> userRoles = new LinkedList<>();
            if (Objects.nonNull(roleDtoList) && !roleDtoList.isEmpty()) {
                roleDtoList.forEach(role -> {
                    Role masterRole = roleRepository.findById(role.getRoleId())
                            .orElseThrow(() -> new ResourceNotFoundException("ROLE_NOT_FOUND"));
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(masterRole);
                    userRoles.add(userRole);
                });
                userRoleRepository.saveAll(userRoles);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Exception in signUp saveRole: " + e.getMessage());
        }
    }

    public User getUserInfo(String email) throws ResourceNotFoundException {
        Optional<User> userOptional = userRepository.findByUserNameAndIsActiveTrue(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new ResourceNotFoundException("Invalid login or password.");
        }
    }

    public UserDto getUserDTOByEmail(String email) {
        List<Role> roleList = new LinkedList<>();
        UserDto userDTO = new UserDto();

        Optional<User> user = userRepository.findByUserNameAndIsActiveTrue(email);
        if (user.isPresent()) {
            List<UserRole> userRoleMappings = userRoleRepository.findByUserId(user.get().getId());
            userRoleMappings.forEach(userRole -> {
                Role role = userRole.getRole();
                roleList.add(role);
            });
            userDTO = modelMapper.map(user.get(), UserDto.class);
            userDTO.setUserRoleList(roleList.stream()
                    .filter(Role::getIsActive)
                    .map(x -> modelMapper.map(x,RoleDto.class)).collect(Collectors.toList()));
        }
        return userDTO;
    }

}
