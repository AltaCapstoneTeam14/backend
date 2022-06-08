package com.alterra.capstone14.service;

import com.alterra.capstone14.config.security.JwtUtils;
import com.alterra.capstone14.domain.dao.ERole;
import com.alterra.capstone14.domain.dao.Role;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.repository.RoleRepository;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

//    public ResponseEntity<Object> getUsers(){
//        List<User> daoList = userRepository.findAll();
//        List<UserDto> dtoList = new ArrayList<>();
//
//        for(User dao:daoList){
//            dtoList.add(UserDto.builder()
//                    .id(dao.getId())
//                    .name(dao.getName())
//                    .email(dao.getEmail())
//                    .password(dao.getPassword())
//                    .build());
//        }
//
//        return Response.build(Response.get("users"), dtoList, null, HttpStatus.OK);
//    }

    public ResponseEntity<Object> updateUser(UserDto userDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        if (!Objects.equals(userDto.getName(), "") && userDto.getName() != null) {
            user.get().setName(userDto.getName());
        }

        if (!Objects.equals(userDto.getEmail(), "") && userDto.getEmail() != null) {
            if (Boolean.TRUE.equals(userRepository.existsByEmail(userDto.getEmail()))) {
                return Response.build(Response.exist("user", "email", userDto.getEmail()), null, null, HttpStatus.BAD_REQUEST);
            }
            user.get().setEmail(userDto.getEmail());
        }

        if (!Objects.equals(userDto.getPassword(), "") && userDto.getPassword() != null) {
            user.get().setPassword(encoder.encode(userDto.getPassword()));
        }

        userRepository.save(user.get());

        UserDto userDto1 = UserDto.builder()
                .id(user.get().getId())
                .name(user.get().getName())
                .email(user.get().getEmail())
                .createdAt(user.get().getCreatedAt())
                .updatedAt(user.get().getUpdatedAt())
                .build();

        return Response.build(Response.update("user"), userDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> deleteUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        userRepository.deleteById(user.get().getId());

        return Response.build(Response.delete("user"), null, null, HttpStatus.CREATED);
    }
}
