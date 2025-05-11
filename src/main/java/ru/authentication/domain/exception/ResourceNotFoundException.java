package ru.authentication.domain.exception;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {

    private Map<String, String> errors;

    /** @exampel entity.UUID -> not such*/
    public static Map<String, String> CreateMessageErrors(String... title) {
        var errors = new HashMap<String, String>();
        for (var elem : title) {
            errors.put(elem, "not such key");
        }
        return errors;
    }

    public ResourceNotFoundException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

}