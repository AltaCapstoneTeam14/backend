package com.alterra.capstone14.service;

import com.alterra.capstone14.config.security.JwtUtils;
import com.alterra.capstone14.constant.ERole;
import com.alterra.capstone14.domain.dao.Balance;
import com.alterra.capstone14.domain.dao.Role;
import com.alterra.capstone14.domain.dao.TopupProduct;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.LoginDto;
import com.alterra.capstone14.domain.dto.TokenDto;
import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.domain.dto.UserNoPwdDto;
import com.alterra.capstone14.repository.BalanceRepository;
import com.alterra.capstone14.repository.RoleRepository;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AuthService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ModelMapper modelMapper;

    public ResponseEntity<Object> registerUser(UserDto userDto) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(userDto.getEmail()))) {
            return Response.build(Response.exist("user", "email", userDto.getEmail()), null, null, HttpStatus.BAD_REQUEST);
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(encoder.encode(userDto.getPassword()));

        Set<Role> roles = new HashSet<>();

        Optional<Role> userRole = roleRepository.findByName(ERole.USER);
        if(userRole.isEmpty()){
            log.info("Role user not found");
            return Response.build("Internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        roles.add(userRole.get());

        user.setRoles(roles);
        userRepository.save(user);

        Balance balance = Balance.builder().user(user).amount(0L).build();
        balanceRepository.save(balance);

        UserNoPwdDto userNoPasswordDto = UserNoPwdDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .build();

        return Response.build("Register success", userNoPasswordDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> loginUser(LoginDto loginDto) {
        if (Boolean.FALSE.equals(userRepository.existsByEmail(loginDto.getEmail()))) {
            return Response.build("email or password incorrect", null, null, HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userRepository.findByEmail(loginDto.getEmail());
        Boolean isPasswordCorrect = encoder.matches(loginDto.getPassword(), user.get().getPassword());
        if (Boolean.FALSE.equals(isPasswordCorrect)) {
            return Response.build("email or password incorrect", null, null, HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        TokenDto token = TokenDto.builder().token(jwt).build();

        return Response.build("Login success", token, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> loginAdmin(LoginDto loginDto) {
        if (Boolean.FALSE.equals(userRepository.existsByEmail(loginDto.getEmail()))) {
            return Response.build("email or password incorrect", null, null, HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userRepository.findByEmail(loginDto.getEmail());
        Optional<Role> adminRole = roleRepository.findByName(ERole.ADMIN);
        if(adminRole.isEmpty()){
            log.info("Role admin not found");
            return Response.build("Internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(!user.get().getRoles().contains(adminRole.get())){
            return Response.build("email or password incorrect", null, null, HttpStatus.BAD_REQUEST);
        }

        Boolean isPasswordCorrect = encoder.matches(loginDto.getPassword(), user.get().getPassword());
        if (Boolean.FALSE.equals(isPasswordCorrect)) {
            return Response.build("email or password incorrect", null, null, HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        TokenDto token = TokenDto.builder().token(jwt).build();

        return Response.build("Login success", token, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> loginSuperAdmin(LoginDto loginDto) {
        if (Boolean.FALSE.equals(userRepository.existsByEmail(loginDto.getEmail()))) {
            return Response.build("email or password incorrect", null, null, HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userRepository.findByEmail(loginDto.getEmail());
        Optional<Role> superAdminRole = roleRepository.findByName(ERole.SUPER_ADMIN);
        if(superAdminRole.isEmpty()){
            log.info("Role super admin not found");
            return Response.build("Internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(!user.get().getRoles().contains(superAdminRole.get())){
            log.info("user don't have super admin role");
            return Response.build("email or password incorrect", null, null, HttpStatus.BAD_REQUEST);
        }

        Boolean isPasswordCorrect = encoder.matches(loginDto.getPassword(), user.get().getPassword());
        if (Boolean.FALSE.equals(isPasswordCorrect)) {
            return Response.build("email or password incorrect", null, null, HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        TokenDto token = TokenDto.builder().token(jwt).build();

        return Response.build("Login success", token, null, HttpStatus.OK);
    }
}
