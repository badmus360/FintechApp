package com.example.money_way_2.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PasswordResetDto {
    @NotNull(message = "Password must not be empty")
    private String currentPassword;

    @NotNull(message = "Password must not be empty")
    private String newPassword;
}
