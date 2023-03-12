package com.example.money_way_2.service.impl;

import com.example.money_way_2.configuration.mail.EmailService;
import com.example.money_way_2.configuration.security.CustomUserDetailService;
import com.example.money_way_2.dto.request.LoginRequestDto;
import com.example.money_way_2.dto.request.PasswordResetDto;
import com.example.money_way_2.dto.request.SignUpDto;
import com.example.money_way_2.dto.request.VerifyTokenDto;
import com.example.money_way_2.dto.response.ApiResponse;
import com.example.money_way_2.exception.ValidationException;
import com.example.money_way_2.model.User;
import com.example.money_way_2.repository.UserRepository;
import com.example.money_way_2.service.UserService;
import com.example.money_way_2.utils.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUtil appUtil;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService customUserDetailService;


    @Override
    public ApiResponse<String> updatePassword(PasswordResetDto passwordResetDto) {
        return null;
    }

    @Override
    public ResponseEntity<String> login(LoginRequestDto request) {

        User users = userRepository.findByEmail(request.getEmail()).orElseThrow(()
                -> new UsernameNotFoundException("User Not Found"));
        if(!users.isActive()) {
            throw new ValidationException("User Not Active");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails user = customUserDetailService.loadUserByUsername(request.getEmail());

        if (user != null) {
            return ResponseEntity.ok(jwtUtils.generateToken(user));
        }
        return ResponseEntity.status(400).body("Some Error Occurred");
    }

    @Override
    public ApiResponse verifyLink(VerifyTokenDto verifyTokenDto) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> SignUp(SignUpDto signUpDto) throws ValidationException {
        return null;
    }
}
