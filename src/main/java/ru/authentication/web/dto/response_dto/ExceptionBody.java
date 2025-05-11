package ru.authentication.web.dto.response_dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor
public class ExceptionBody implements Serializable {

    private String message;
    private Map<String, String> errors;

    public ExceptionBody(String message) {
        this.message = message;
    }

    public ExceptionBody(Exception e) {
        this.message = e.getMessage();
        String string = e.getClass().toString();
        System.out.println(string);
        var split = string.split("\\.");

        this.errors = Collections.singletonMap(split[split.length - 1], e.getMessage());
    }

    @Override
    public String toString() {
        return errors != null
                ? "ExceptionBody{" +
                "message='" + message + '\'' +
                ", errors=" + errors +
                '}'
                : "ExceptionBody{" +
                "message='" + message;
    }


}
