package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.EWeeklyLoginStatus;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dao.WeeklyLogin;
import com.alterra.capstone14.domain.dto.WeeklyLoginDto;
import com.alterra.capstone14.repository.CoinRepository;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.repository.WeeklyLoginRepository;
import com.alterra.capstone14.util.DateCurrent;
import com.alterra.capstone14.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class WeeklyLoginService {
    @Autowired
    WeeklyLoginRepository weeklyLoginRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CoinRepository coinRepository;

    @Autowired
    DateCurrent dateCurrent;

    public ResponseEntity<Object> getStatus(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Optional<WeeklyLogin> weeklyLogin = weeklyLoginRepository.findByUserAndStartDate(user.get().getId(), dateCurrent.getMonday());
        if(weeklyLogin.isEmpty()){
            WeeklyLogin newWeeklyLogin = WeeklyLogin.builder()
                    .user(user.get())
                    .startDate(dateCurrent.getMonday())
                    .loginCount(0)
                    .lastLogin(0)
                    .build();

            weeklyLoginRepository.save(newWeeklyLogin);
            weeklyLogin = Optional.of(newWeeklyLogin);
        }

        WeeklyLoginDto weeklyLoginDto = WeeklyLoginDto.builder()
                .id(weeklyLogin.get().getId())
                .startDate(weeklyLogin.get().getStartDate())
                .loginCount(weeklyLogin.get().getLoginCount())
                .lastLogin(weeklyLogin.get().getLastLogin())
                .build();

        if(weeklyLogin.get().getLastLogin().equals(dateCurrent.getDay())){
            weeklyLoginDto.setStatus(EWeeklyLoginStatus.CLAIMED.value);
        }else{
            weeklyLoginDto.setStatus(EWeeklyLoginStatus.NOT_CLAIMED.value);
        }

        return Response.build(Response.get("daily login status"), weeklyLoginDto, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> getBonus(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Optional<WeeklyLogin> weeklyLogin = weeklyLoginRepository.findByUserAndStartDate(user.get().getId(), dateCurrent.getMonday());
        if(weeklyLogin.isEmpty()){
            WeeklyLogin newWeeklyLogin = WeeklyLogin.builder()
                    .user(user.get())
                    .startDate(dateCurrent.getMonday())
                    .loginCount(0)
                    .lastLogin(0)
                    .build();

            weeklyLoginRepository.save(newWeeklyLogin);
            weeklyLogin = Optional.of(newWeeklyLogin);
        }

        Long coinReward = 0L;
        if(!weeklyLogin.get().getLastLogin().equals(dateCurrent.getDay())){
            weeklyLogin.get().setLastLogin(dateCurrent.getDay());
            weeklyLogin.get().setLoginCount(weeklyLogin.get().getLoginCount()+1);
            weeklyLoginRepository.save(weeklyLogin.get());

            if(dateCurrent.getDay() == 7 && weeklyLogin.get().getLoginCount() == 7){
                coinReward = 500L;
            }else{
                coinReward = 100L;
            }
            user.get().getCoin().setAmount(user.get().getCoin().getAmount()+coinReward);
            userRepository.save(user.get());
        }

        WeeklyLoginDto weeklyLoginDto = WeeklyLoginDto.builder()
                .id(weeklyLogin.get().getId())
                .startDate(weeklyLogin.get().getStartDate())
                .loginCount(weeklyLogin.get().getLoginCount())
                .lastLogin(weeklyLogin.get().getLastLogin())
                .coinReward(coinReward)
                .status(EWeeklyLoginStatus.CLAIMED.value)
                .build();

        return Response.build("Claim daily login success", weeklyLoginDto, null, HttpStatus.CREATED);
    }
}
