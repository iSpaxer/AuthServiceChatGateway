package ru.authentication.web.controller.advice;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.authentication.domain.exception.ResourceNotFoundException;
import ru.authentication.web.dto.response_dto.ExceptionBody;


import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestControllerAdvice_ {

    @ApiResponse(
            responseCode = "400",
            description = "Bad request"
    )
    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<ExceptionBody> handleBadCredentials(AuthenticationException e) {
        printExc(e);
        var body = new ExceptionBody("Authentication failed. Bad credentials." + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

//    @ExceptionHandler(CreateException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ExceptionBody handleCreateException(CreateException e) {
//        return new ExceptionBody(e.getMessage(), e.getErrors());
//    }

//    @ExceptionHandler(UpdateException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ExceptionBody handleUpdateException(UpdateException e) {
//        return new ExceptionBody(e.getMessage(), e.getErrors());
//    }



//    @ExceptionHandler(JwtException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ExceptionBody handleJwtException(JwtException e) {
//        return new ExceptionBody(e.getMessage());
//    }
//


//    @ExceptionHandler(SignatureException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ExceptionBody handleSignatureException(SignatureException e) {
//        return new ExceptionBody(
//                e.getMessage(),
//                Collections.singletonMap("JWT not valid", e.getMessage().substring(0, e.getMessage().indexOf(".") + 1))
//
//        );
//    }

//    @ExceptionHandler(ExpiredJwtException.class) todo
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ExceptionBody handleExpiredJwtException(ExpiredJwtException e) {
//
//        return new ExceptionBody(
//                e.getMessage(),
//                Collections.singletonMap("JWT expired", e.getMessage().substring(0, e.getMessage().indexOf(".")))
//        );
//    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionBody> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        printExc(e);
        Map<String, String> errors = new HashMap<>();
        errors.put("Not allowed method", e.getMethod());
        errors.putAll(e.getHeaders().toSingleValueMap());
        return ResponseEntity
                .status(e.getBody().getStatus())
                .body(new ExceptionBody(
                        e.getMessage(),
                        errors
                ));
    }



    @ExceptionHandler(LoginException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleLoginException(LoginException e) {
        printExc(e);
        return new ExceptionBody(e.getMessage());
    }


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleAccessDenied(AccessDeniedException e) {
        printExc(e);
        return new ExceptionBody("Access. Denied.");
    }

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMethodArgumentNotValidException(MissingPathVariableException e ) {
        printExc(e);
        return new ExceptionBody(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleConstraintViolation(ConstraintViolationException e) {
        printExc(e);
        return new ExceptionBody(
                e.getMessage(),
                e.getConstraintViolations()
                        .stream()
                        .collect(Collectors.toMap(
                                violation -> violation.getPropertyPath().toString(),
                                ConstraintViolation::getMessage
                        ))
        );
    }



    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleCommonException(DataIntegrityViolationException e) {
        printExc(e);
        return new ExceptionBody(e.getMessage());
    }


    @ExceptionHandler(HibernateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleHibernateException(HibernateException e) {
        printExc(e);
        e.printStackTrace(System.out);
        return new ExceptionBody("INTERNAL_SERVER_ERROR", Collections.singletonMap("HibernateException", e.getMessage()));
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        printExc(e);
        return new ExceptionBody(
                "HTTP-запрос не может быть прочитан или преобразован в объект из-за не соответствующего формата JSON.",
                Collections.singletonMap("message", e.getMessage())
        );
    }

    @ApiResponse(responseCode = "404")
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleResourceNotFound(ResourceNotFoundException e) {
        printExc(e);
        var body = new ExceptionBody(e.getMessage(), e.getErrors());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .body(body);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMethodArgumentNotValidException(MethodArgumentNotValidException e ) {
        printExc(e);
        return new ExceptionBody(
                "Ошибка с проверкой аргумента у переданного объекта.",
                !e.getFieldErrors().isEmpty()
                        ? e.getFieldErrors().stream()
                            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage))
                        : e.getAllErrors().stream().collect(Collectors.toMap(ObjectError::getObjectName, ObjectError::getDefaultMessage)
                )
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody handleNoResourceFoundException(NoResourceFoundException e) {
        printExc(e);
        return new ExceptionBody(
                e.getBody().getTitle(),
                Collections.singletonMap(e.getBody().getTitle(), e.getBody().getDetail())
        );
    }

    @ExceptionHandler({MultipartException.class, org.springframework.web.multipart.support.MissingServletRequestPartException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMultipartException(Exception e) {
        printExc(e);
        return new ExceptionBody(
                e.getMessage(),
                Collections.singletonMap("Bad request", e.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleCommonException(Exception e) {
        printExc(e);
        return new ExceptionBody(
                "INTERNAL_SERVER_ERROR",
                Collections.singletonMap(e.toString().substring(0, e.toString().indexOf(":")), e.getMessage())
        );
    }

    private void printExc(Exception e) {
        log.info("My log error: " + e.toString() + " "  + e.getMessage());
    }

}
