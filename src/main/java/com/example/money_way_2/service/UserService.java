package com.example.money_way_2.service;

import com.example.money_way_2.dto.request.LoginRequestDto;
import com.example.money_way_2.dto.request.PasswordResetDto;
import com.example.money_way_2.dto.request.SignUpDto;
import com.example.money_way_2.dto.request.VerifyTokenDto;
import com.example.money_way_2.dto.response.ApiResponse;
import com.example.money_way_2.exception.ValidationException;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ApiResponse<String> updatePassword(PasswordResetDto passwordResetDto);

    ResponseEntity<String> login(LoginRequestDto request);

    ApiResponse verifyLink(VerifyTokenDto verifyTokenDto);

    ResponseEntity<ApiResponse> SignUp(SignUpDto signUpDto) throws ValidationException;
}
