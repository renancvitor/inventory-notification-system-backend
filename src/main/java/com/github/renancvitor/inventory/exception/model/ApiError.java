package com.github.renancvitor.inventory.exception.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApiError {

    private String timestamp;
    private Integer status;
    private String error;
    private String message;
    private List<DataValidationError> errors;

    public ApiError(HttpStatus status, String message) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
    }

    public ApiError(HttpStatus status, String message, List<DataValidationError> errors) {
        this(status, message);
        this.errors = errors;
    }

}
