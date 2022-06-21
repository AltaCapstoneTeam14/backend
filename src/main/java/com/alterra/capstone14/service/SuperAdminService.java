package com.alterra.capstone14.service;

import com.alterra.capstone14.config.security.JwtUtils;
import com.alterra.capstone14.constant.ERole;
import com.alterra.capstone14.domain.dao.Role;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.EmailDto;
import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.repository.RoleRepository;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class SuperAdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    public ResponseEntity<Object> addAdminRole(EmailDto emailDto) {
        Optional<User> user = userRepository.findByEmail(emailDto.getEmail());
        if(user.isEmpty()){
            return Response.build(Response.notFound("user"), null, null, HttpStatus.BAD_REQUEST);
        }

        Optional<Role> adminRole = roleRepository.findByName(ERole.ADMIN);
        if(adminRole.isEmpty()){
            log.info("Role admin not found");
            return Response.build("Internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Set<Role> roles = user.get().getRoles();
        roles.add(adminRole.get());

        user.get().setRoles(roles);
        userRepository.save(user.get());

        UserDto userDto1 = UserDto.builder()
                .id(user.get().getId())
                .name(user.get().getName())
                .email(user.get().getEmail())
                .createdAt(user.get().getCreatedAt())
                .build();

        return Response.build(Response.update("user"), userDto1, null, HttpStatus.CREATED);
    }
}
