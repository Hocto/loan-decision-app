package com.example.loanapp.exception;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GenericExceptionHandler {

    private final Gson gson;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException exception) {

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(
                gson.toJson(generateExceptionList(exception))
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleValidationException(EntityNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON).body(
                        gson.toJson(
                                EntityNotFoundExceptionResponse.builder()
                                        .entity(exception.getEntity())
                                        .message(exception.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleGeneralException(Throwable throwable) {

        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(
                gson.toJson(GeneralServerExceptionResponse.builder()
                        .type(throwable.getClass().getSimpleName())
                        .message(throwable.getMessage())
                        .build())
        );
    }

    private List<MethodArgumentNotValidResponse> generateExceptionList(MethodArgumentNotValidException ex) {

        return ex.getBindingResult().getFieldErrors().stream().map(
                error -> MethodArgumentNotValidResponse.builder()
                        .field(error.getField())
                        .reason(error.getCode())
                        .message(error.getDefaultMessage())
                        .build()
        ).collect(Collectors.toList());
    }

    @Builder
    private static class EntityNotFoundExceptionResponse {
        private String entity;
        private String message;
    }

    @Builder
    private static class GeneralServerExceptionResponse {
        private String message;
        private String type;
    }

    @Builder
    private static class MethodArgumentNotValidResponse {
        private String field;
        private String reason;
        private String message;
    }
}
