package ru.authentication.web.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class UserDto implements Serializable {

    @NotNull
    String username;

    @NotNull
    String password;

}
