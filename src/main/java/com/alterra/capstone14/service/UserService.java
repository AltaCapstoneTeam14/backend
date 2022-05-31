package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public ResponseEntity<Object> getUsers(){
        List<User> daoList = userRepository.findAll();
        List<UserDto> dtoList = new ArrayList<>();

        for(User dao:daoList){
            dtoList.add(UserDto.builder()
                    .id(dao.getId())
                    .name(dao.getName())
                    .email(dao.getEmail())
                    .password(dao.getPassword())
                    .build());
        }

        return Response.build(Response.get("users"), dtoList, null, HttpStatus.OK);
    }

}
