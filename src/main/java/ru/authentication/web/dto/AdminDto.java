package ru.authentication.web.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {

    /**
     * Уникальная строка, по которой входим в аккаунт
     */
    @Email
    @Column(unique = true, nullable = false)
    String email;

    @NotNull
    String password;


}
