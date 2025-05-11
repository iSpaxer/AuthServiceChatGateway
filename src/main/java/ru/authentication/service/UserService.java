package ru.authentication.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.authentication.domain.entity.User;
import ru.authentication.domain.exception.ResourceNotFoundException;
import ru.authentication.repository.UserRepository;
import ru.authentication.secure.JwtUserDetails;
import ru.authentication.web.dto.UserDto;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository repository;
    PasswordEncoder passwordEncoder;

    public User getUserById(long id) {
        return repository.findById(id).get();
    }

    public User create(UserDto dto) {
        return repository.save(mapToEntity(dto));
    }

    public User update(UserDto dto) {
        return repository.save(mapToEntity(dto));
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    private User mapToEntity(UserDto dto) {
        return User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
    }

    public UserDetails getUserDetailsByEmail(String username) {
        var user = repository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found!"));
        return new JwtUserDetails(user.getUsername(), user.getPassword());
    }
}
